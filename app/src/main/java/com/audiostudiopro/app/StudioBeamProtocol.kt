package com.audiostudiopro.app

import android.content.Context
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.OpenableColumns
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

/**
 * StudioBeam (ASP1 - AudioStudio Protocol v1)
 *
 * A custom, high-speed, zero-permission binary framing transfer protocol designed for
 * multi-song local Wi-Fi & Mobile Hotspot sharing between two AudioStudio Pro devices.
 *
 * Wire Format (TCP):
 *   1. Handshake (Receiver -> Sender): 4B "HELO" + UTF(pin)
 *   2. Gate (Sender -> Receiver):      4B "ASP1" (accepted) or "DENY" (wrong PIN)
 *   3. Batch Header:                   1B version(1) + UTF(deviceName) + 4B trackCount
 *   4. Per Track Frame:                4B metaLen + UTF-8 JSON meta + 8B payloadLen + raw audio bytes (64KB chunks)
 *   5. Completion ACK (Receiver->Snd): 4B "OKAY"
 *
 * Discovery:
 *   - UDP Broadcast Beacon on Port 47821 for automatic nearby radar detection
 *   - Compact Base36 Beam Code (encodes IPv4 + TCP Port) for isolated campus Wi-Fi fallback
 */

enum class BeamMode { IDLE, SENDING, RECEIVING, COMPLETED, ERROR }

data class DiscoveredBeamPeer(
    val hostIp: String,
    val port: Int,
    val deviceName: String,
    val trackCount: Int,
    val totalBytes: Long,
    val requiresPin: Boolean,
    val lastSeenMs: Long = System.currentTimeMillis()
)

data class StudioBeamUiState(
    val mode: BeamMode = BeamMode.IDLE,
    val isBroadcasting: Boolean = false,
    val isScanning: Boolean = false,
    val localIp: String = "",
    val activePort: Int = 0,
    val beamCode: String = "",
    val sessionPin: String = "",
    val requirePin: Boolean = false,
    val discoveredPeers: List<DiscoveredBeamPeer> = emptyList(),
    val statusText: String = "Ready to transfer at full Wi-Fi speed",
    val currentTrackTitle: String = "",
    val completedTracks: Int = 0,
    val totalTracks: Int = 0,
    val bytesTransferred: Long = 0L,
    val totalBytes: Long = 0L,
    val speedMbps: Float = 0f,
    val errorMessage: String? = null
) {
    val progressFraction: Float
        get() = when {
            totalBytes > 0L -> (bytesTransferred.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
            totalTracks > 0 -> (completedTracks.toFloat() / totalTracks.toFloat()).coerceIn(0f, 1f)
            else -> 0f
        }
}

object BeamCodeCodec {
    /**
     * Packs IPv4 (32 bits) + TCP Port (16 bits) into a compact 10-char Base36 code (XXXXX-XXXXX).
     */
    fun encode(ipv4: String, port: Int): String {
        val parts = ipv4.split(".")
        if (parts.size != 4) return "$ipv4:$port"
        val octets = parts.map { it.toIntOrNull()?.coerceIn(0, 255) ?: return "$ipv4:$port" }
        val ipBits = (octets[0].toLong() shl 24) or
            (octets[1].toLong() shl 16) or
            (octets[2].toLong() shl 8) or
            octets[3].toLong()
        val packed = (ipBits shl 16) or (port.toLong() and 0xFFFFL)
        val raw = packed.toString(36).uppercase(Locale.US).padStart(10, '0')
        return "${raw.substring(0, 5)}-${raw.substring(5, 10)}"
    }

    /**
     * Accepts either a StudioBeam Code ("K4P9X-2M7Q1") or a raw "192.168.43.1:47822" / "192.168.43.1".
     */
    fun decode(input: String, defaultPort: Int = StudioBeamEngine.DEFAULT_TCP_PORT): Pair<String, Int>? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return null

        // Direct IPv4 or IPv4:Port support
        if (trimmed.contains(".")) {
            val hostPart = trimmed.substringBefore(":").trim()
            val portPart = if (trimmed.contains(":")) {
                trimmed.substringAfter(":").trim().toIntOrNull() ?: defaultPort
            } else {
                defaultPort
            }
            val octets = hostPart.split(".")
            if (octets.size == 4 && octets.all { it.toIntOrNull() in 0..255 } && portPart in 1..65535) {
                return hostPart to portPart
            }
            return null
        }

        // Base36 Beam Code decode
        val clean = trimmed.replace("-", "").replace(" ", "").uppercase(Locale.US)
        val packed = clean.toLongOrNull(36) ?: return null
        val port = (packed and 0xFFFFL).toInt()
        val ipBits = packed ushr 16
        val o1 = ((ipBits ushr 24) and 0xFFL).toInt()
        val o2 = ((ipBits ushr 16) and 0xFFL).toInt()
        val o3 = ((ipBits ushr 8) and 0xFFL).toInt()
        val o4 = (ipBits and 0xFFL).toInt()
        if (o1 == 0 || port !in 1..65535) return null
        return "$o1.$o2.$o3.$o4" to port
    }
}

class StudioBeamEngine(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(StudioBeamUiState())
    val state: StateFlow<StudioBeamUiState> = _state.asStateFlow()

    private var serverJob: Job? = null
    private var beaconJob: Job? = null
    private var scanJob: Job? = null
    private var receiveJob: Job? = null

    @Volatile
    private var activeServerSocket: ServerSocket? = null

    fun setRequirePin(requirePin: Boolean) {
        _state.update { it.copy(requirePin = requirePin) }
    }

    fun startSending(tracks: List<AudioTrack>) {
        if (tracks.isEmpty()) {
            _state.update { it.copy(mode = BeamMode.ERROR, errorMessage = "Select at least one track to send.") }
            return
        }
        stopAll()

        val pin = String.format(Locale.US, "%04d", Random.nextInt(1000, 10000))
        val deviceName = resolveDeviceName()

        serverJob = scope.launch(Dispatchers.IO) {
            try {
                val (localIp, broadcastIp) = findLocalNetworkAddresses()
                if (localIp == null) {
                    _state.update {
                        it.copy(
                            mode = BeamMode.ERROR,
                            isBroadcasting = false,
                            errorMessage = "Connect both phones to the same Wi-Fi or turn on a Mobile Hotspot."
                        )
                    }
                    return@launch
                }

                val estimatedTotalBytes = tracks.sumOf { estimateTrackSize(it) }.coerceAtLeast(0L)
                val server = try {
                    ServerSocket(DEFAULT_TCP_PORT)
                } catch (_: Exception) {
                    ServerSocket(0)
                }
                activeServerSocket = server
                val boundPort = server.localPort
                val code = BeamCodeCodec.encode(localIp, boundPort)

                _state.update {
                    it.copy(
                        mode = BeamMode.SENDING,
                        isBroadcasting = true,
                        localIp = localIp,
                        activePort = boundPort,
                        beamCode = code,
                        sessionPin = pin,
                        statusText = "Waiting for receiver on Wi-Fi / Hotspot…",
                        currentTrackTitle = "",
                        completedTracks = 0,
                        totalTracks = tracks.size,
                        bytesTransferred = 0L,
                        totalBytes = estimatedTotalBytes,
                        speedMbps = 0f,
                        errorMessage = null
                    )
                }

                // Launch UDP discovery beacon so nearby receivers see this device automatically
                beaconJob = launch(Dispatchers.IO) {
                    DatagramSocket().use { udp ->
                        udp.broadcast = true
                        while (isActive) {
                            val currentReqPin = _state.value.requirePin
                            val payload = JSONObject().apply {
                                put("magic", BEACON_MAGIC)
                                put("name", deviceName)
                                put("port", boundPort)
                                put("count", tracks.size)
                                put("bytes", estimatedTotalBytes)
                                put("pin", currentReqPin)
                            }.toString().toByteArray(Charsets.UTF_8)

                            val targets = buildSet {
                                add("255.255.255.255")
                                if (!broadcastIp.isNullOrBlank()) add(broadcastIp)
                            }
                            for (target in targets) {
                                runCatching {
                                    val pkt = DatagramPacket(
                                        payload,
                                        payload.size,
                                        InetAddress.getByName(target),
                                        DISCOVERY_UDP_PORT
                                    )
                                    udp.send(pkt)
                                }
                            }
                            delay(1100L)
                        }
                    }
                }

                // Accept connections cleanly until session stopped
                while (isActive && !server.isClosed) {
                    val client = try {
                        server.accept()
                    } catch (_: Exception) {
                        break
                    }
                    handleSenderClient(client, deviceName, tracks)
                }
            } catch (_: CancellationException) {
                // Expected on user cancel
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        mode = BeamMode.ERROR,
                        isBroadcasting = false,
                        errorMessage = e.localizedMessage ?: "StudioBeam server error"
                    )
                }
            } finally {
                beaconJob?.cancel()
                runCatching { activeServerSocket?.close() }
                activeServerSocket = null
            }
        }
    }

    private fun handleSenderClient(client: Socket, deviceName: String, tracks: List<AudioTrack>) {
        client.use { socket ->
            socket.tcpNoDelay = true
            socket.soTimeout = 30_000

            val dis = DataInputStream(BufferedInputStream(socket.getInputStream()))
            val dos = DataOutputStream(BufferedOutputStream(socket.getOutputStream(), BUFFER_SIZE))

            // 1. Handshake
            val heloBytes = ByteArray(4)
            dis.readFully(heloBytes)
            if (String(heloBytes, Charsets.US_ASCII) != "HELO") return
            val enteredPin = dis.readUTF()

            val currentState = _state.value
            if (currentState.requirePin && enteredPin != currentState.sessionPin) {
                dos.write("DENY".toByteArray(Charsets.US_ASCII))
                dos.flush()
                return
            }

            // 2. Accept & send ASP1 Batch Header
            dos.write("ASP1".toByteArray(Charsets.US_ASCII))
            dos.writeByte(1)
            dos.writeUTF(deviceName)
            dos.writeInt(tracks.size)
            dos.flush()

            val startNs = System.nanoTime()
            var totalSentBytes = 0L
            val chunk = ByteArray(BUFFER_SIZE)

            tracks.forEachIndexed { index, track ->
                _state.update {
                    it.copy(
                        statusText = "Sending ${index + 1} of ${tracks.size}…",
                        currentTrackTitle = track.title
                    )
                }

                val prepared = prepareTrackStream(track)
                try {
                    val metaJson = JSONObject().apply {
                        put("title", track.title)
                        put("artist", track.artist)
                        put("album", track.album)
                        put("durationMs", track.durationMs)
                        put("ext", prepared.extension)
                    }.toString().toByteArray(Charsets.UTF_8)

                    dos.writeInt(metaJson.size)
                    dos.write(metaJson)
                    dos.writeLong(prepared.exactSize)

                    var remaining = prepared.exactSize
                    var lastUiEmit = System.currentTimeMillis()
                    while (remaining > 0L) {
                        val toRead = minOf(chunk.size.toLong(), remaining).toInt()
                        val read = prepared.stream.read(chunk, 0, toRead)
                        if (read <= 0) break
                        dos.write(chunk, 0, read)
                        remaining -= read
                        totalSentBytes += read

                        val now = System.currentTimeMillis()
                        if (now - lastUiEmit >= 120L) {
                            lastUiEmit = now
                            val elapsedSec = ((System.nanoTime() - startNs) / 1_000_000_000.0).coerceAtLeast(0.05)
                            val mbPerSec = (totalSentBytes / (1024.0 * 1024.0) / elapsedSec).toFloat()
                            _state.update {
                                it.copy(
                                    bytesTransferred = totalSentBytes,
                                    speedMbps = mbPerSec
                                )
                            }
                        }
                    }
                    dos.flush()
                } finally {
                    runCatching { prepared.stream.close() }
                    prepared.tempFile?.let { runCatching { it.delete() } }
                }

                _state.update {
                    it.copy(
                        completedTracks = index + 1,
                        bytesTransferred = totalSentBytes
                    )
                }
            }

            // Wait for receiver ACK so the socket doesn't truncate trailing TCP buffers
            runCatching {
                val ack = ByteArray(4)
                dis.readFully(ack)
            }

            val elapsedSec = ((System.nanoTime() - startNs) / 1_000_000_000.0).coerceAtLeast(0.05)
            val avgSpeed = (totalSentBytes / (1024.0 * 1024.0) / elapsedSec).toFloat()
            _state.update {
                it.copy(
                    mode = BeamMode.COMPLETED,
                    isBroadcasting = false,
                    statusText = "Sent ${tracks.size} track${if (tracks.size == 1) "" else "s"} at ${String.format(Locale.US, "%.1f", avgSpeed)} MB/s",
                    speedMbps = avgSpeed,
                    completedTracks = tracks.size,
                    totalBytes = maxOf(it.totalBytes, totalSentBytes),
                    bytesTransferred = totalSentBytes
                )
            }
            beaconJob?.cancel()
            runCatching { activeServerSocket?.close() }
        }
    }

    fun startScanningForPeers() {
        scanJob?.cancel()
        scanJob = scope.launch(Dispatchers.IO) {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val multicastLock = runCatching {
                wifiManager?.createMulticastLock("AudioStudioBeamLock")?.apply {
                    setReferenceCounted(true)
                    acquire()
                }
            }.getOrNull()

            _state.update { it.copy(isScanning = true, errorMessage = null) }

            try {
                DatagramSocket(null).use { socket ->
                    socket.reuseAddress = true
                    socket.broadcast = true
                    socket.bind(InetSocketAddress(DISCOVERY_UDP_PORT))
                    socket.soTimeout = 1000

                    val buf = ByteArray(2048)
                    val peersMap = LinkedHashMap<String, DiscoveredBeamPeer>()

                    while (isActive) {
                        val packet = DatagramPacket(buf, buf.size)
                        try {
                            socket.receive(packet)
                            val raw = String(packet.data, 0, packet.length, Charsets.UTF_8)
                            val json = JSONObject(raw)
                            if (json.optString("magic") == BEACON_MAGIC) {
                                val hostIp = packet.address.hostAddress ?: continue
                                val port = json.optInt("port", DEFAULT_TCP_PORT)
                                val key = "$hostIp:$port"
                                peersMap[key] = DiscoveredBeamPeer(
                                    hostIp = hostIp,
                                    port = port,
                                    deviceName = json.optString("name", "AudioStudio Device"),
                                    trackCount = json.optInt("count", 0),
                                    totalBytes = json.optLong("bytes", 0L),
                                    requiresPin = json.optBoolean("pin", false),
                                    lastSeenMs = System.currentTimeMillis()
                                )
                            }
                        } catch (_: SocketTimeoutException) {
                            // Periodic expiry sweep
                        } catch (_: Exception) {
                            // Ignore malformed packets
                        }

                        val cutoff = System.currentTimeMillis() - 4500L
                        peersMap.entries.removeAll { it.value.lastSeenMs < cutoff }
                        _state.update { it.copy(discoveredPeers = peersMap.values.toList()) }
                    }
                }
            } catch (_: CancellationException) {
                // Expected
            } catch (_: Exception) {
                // Manual Beam Code still works if UDP port is occupied
            } finally {
                runCatching {
                    if (multicastLock?.isHeld == true) multicastLock.release()
                }
                _state.update { it.copy(isScanning = false) }
            }
        }
    }

    fun stopScanning() {
        scanJob?.cancel()
        scanJob = null
        _state.update { it.copy(isScanning = false) }
    }

    fun receiveFromPeer(
        hostIp: String,
        port: Int,
        pin: String,
        expectedTotalBytes: Long = 0L,
        onTrackReceived: (AudioTrack) -> Unit
    ) {
        stopScanning()
        receiveJob?.cancel()

        receiveJob = scope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(
                    mode = BeamMode.RECEIVING,
                    statusText = "Connecting to $hostIp…",
                    currentTrackTitle = "",
                    completedTracks = 0,
                    totalTracks = 0,
                    bytesTransferred = 0L,
                    totalBytes = expectedTotalBytes,
                    speedMbps = 0f,
                    errorMessage = null
                )
            }

            try {
                Socket().use { socket ->
                    socket.tcpNoDelay = true
                    socket.connect(InetSocketAddress(hostIp, port), 8_000)
                    socket.soTimeout = 30_000

                    val dos = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
                    val dis = DataInputStream(BufferedInputStream(socket.getInputStream(), BUFFER_SIZE))

                    // 1. Send HELO + PIN
                    dos.write("HELO".toByteArray(Charsets.US_ASCII))
                    dos.writeUTF(pin.trim())
                    dos.flush()

                    // 2. Read Gate
                    val gate = ByteArray(4)
                    dis.readFully(gate)
                    val gateStr = String(gate, Charsets.US_ASCII)
                    if (gateStr == "DENY") {
                        _state.update {
                            it.copy(
                                mode = BeamMode.ERROR,
                                errorMessage = "Incorrect 4-digit Beam PIN. Check the sender's screen."
                            )
                        }
                        return@launch
                    }
                    if (gateStr != "ASP1") {
                        throw IllegalStateException("Unsupported protocol header: $gateStr")
                    }

                    val version = dis.readByte().toInt()
                    if (version != 1) throw IllegalStateException("Unsupported ASP version: $version")

                    val senderName = dis.readUTF()
                    val trackCount = dis.readInt()
                    if (trackCount <= 0) throw IllegalStateException("Sender has no tracks queued.")

                    val receivedDir = File(context.filesDir, "received_music").apply {
                        if (!exists()) mkdirs()
                    }

                    val startNs = System.nanoTime()
                    var totalReceivedBytes = 0L
                    val chunk = ByteArray(BUFFER_SIZE)

                    _state.update {
                        it.copy(
                            statusText = "Receiving from $senderName…",
                            totalTracks = trackCount
                        )
                    }

                    for (i in 0 until trackCount) {
                        val metaLen = dis.readInt()
                        val metaBytes = ByteArray(metaLen)
                        dis.readFully(metaBytes)
                        val meta = JSONObject(String(metaBytes, Charsets.UTF_8))
                        val title = meta.optString("title", "Received Track")
                        val artist = meta.optString("artist", "Unknown Artist")
                        val album = meta.optString("album", "StudioBeam")
                        val rawExt = meta.optString("ext", "mp3")
                            .lowercase(Locale.US)
                            .filter { it.isLetterOrDigit() }
                            .ifBlank { "mp3" }

                        val fileSize = dis.readLong()
                        val outFile = File(receivedDir, "${UUID.randomUUID()}.$rawExt")

                        _state.update {
                            it.copy(
                                statusText = "Receiving ${i + 1} of $trackCount from $senderName…",
                                currentTrackTitle = title
                            )
                        }

                        FileOutputStream(outFile).use { fos ->
                            var remaining = fileSize
                            var lastUiEmit = System.currentTimeMillis()
                            while (remaining > 0L) {
                                val toRead = minOf(chunk.size.toLong(), remaining).toInt()
                                val read = dis.read(chunk, 0, toRead)
                                if (read < 0) throw IllegalStateException("Connection dropped mid-transfer.")
                                fos.write(chunk, 0, read)
                                remaining -= read
                                totalReceivedBytes += read

                                val now = System.currentTimeMillis()
                                if (now - lastUiEmit >= 120L) {
                                    lastUiEmit = now
                                    val elapsedSec = ((System.nanoTime() - startNs) / 1_000_000_000.0).coerceAtLeast(0.05)
                                    val mbPerSec = (totalReceivedBytes / (1024.0 * 1024.0) / elapsedSec).toFloat()
                                    _state.update {
                                        it.copy(
                                            bytesTransferred = totalReceivedBytes,
                                            speedMbps = mbPerSec
                                        )
                                    }
                                }
                            }
                            fos.flush()
                        }

                        val importedTrack = MetadataExtractor.extractTrackFromFile(
                            context = context,
                            file = outFile,
                            overrideTitle = title,
                            overrideArtist = artist,
                            overrideAlbum = album
                        )
                        withContext(Dispatchers.Main) {
                            onTrackReceived(importedTrack)
                        }

                        _state.update {
                            it.copy(
                                completedTracks = i + 1,
                                bytesTransferred = totalReceivedBytes
                            )
                        }
                    }

                    // Send completion ACK
                    dos.write("OKAY".toByteArray(Charsets.US_ASCII))
                    dos.flush()

                    val elapsedSec = ((System.nanoTime() - startNs) / 1_000_000_000.0).coerceAtLeast(0.05)
                    val avgSpeed = (totalReceivedBytes / (1024.0 * 1024.0) / elapsedSec).toFloat()

                    _state.update {
                        it.copy(
                            mode = BeamMode.COMPLETED,
                            statusText = "Imported $trackCount track${if (trackCount == 1) "" else "s"} at ${String.format(Locale.US, "%.1f", avgSpeed)} MB/s",
                            speedMbps = avgSpeed,
                            completedTracks = trackCount,
                            totalBytes = maxOf(it.totalBytes, totalReceivedBytes),
                            bytesTransferred = totalReceivedBytes
                        )
                    }
                }
            } catch (_: CancellationException) {
                // User cancelled
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        mode = BeamMode.ERROR,
                        errorMessage = e.localizedMessage ?: "Could not connect to sender. Verify both phones share Wi-Fi or Hotspot."
                    )
                }
            }
        }
    }

    fun stopAll() {
        beaconJob?.cancel()
        serverJob?.cancel()
        scanJob?.cancel()
        receiveJob?.cancel()
        beaconJob = null
        serverJob = null
        scanJob = null
        receiveJob = null
        runCatching { activeServerSocket?.close() }
        activeServerSocket = null
        _state.update {
            StudioBeamUiState(requirePin = it.requirePin)
        }
    }

    private data class PreparedStream(
        val stream: InputStream,
        val exactSize: Long,
        val extension: String,
        val tempFile: File?
    )

    private fun prepareTrackStream(track: AudioTrack): PreparedStream {
        val uri = Uri.parse(track.uriString)
        val ext = guessExtension(track, uri)

        if (uri.scheme == "file" && uri.path != null) {
            val file = File(uri.path!!)
            return PreparedStream(FileInputStream(file), file.length(), ext, null)
        }

        val afdSize = runCatching {
            context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length }
        }.getOrNull() ?: -1L

        if (afdSize > 0L) {
            val input = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Cannot read track: ${track.title}")
            return PreparedStream(input, afdSize, ext, null)
        }

        // Reliable fallback: copy into cache temp file so exact byte length is guaranteed
        val temp = File.createTempFile("beam_", ".$ext", context.cacheDir)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(temp).use { output -> input.copyTo(output, BUFFER_SIZE) }
        } ?: throw IllegalStateException("Cannot read track: ${track.title}")
        return PreparedStream(FileInputStream(temp), temp.length(), ext, temp)
    }

    private fun estimateTrackSize(track: AudioTrack): Long {
        val uri = Uri.parse(track.uriString)
        if (uri.scheme == "file" && uri.path != null) {
            return File(uri.path!!).length()
        }
        val afdLen = runCatching {
            context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length }
        }.getOrNull()
        if (afdLen != null && afdLen > 0L) return afdLen

        return runCatching {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { c ->
                if (c.moveToFirst()) c.getLong(0) else 0L
            } ?: 0L
        }.getOrDefault(0L)
    }

    private fun guessExtension(track: AudioTrack, uri: Uri): String {
        val fromName = if (uri.scheme == "file") {
            uri.lastPathSegment?.substringAfterLast('.', "")
        } else {
            runCatching {
                context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
                    if (c.moveToFirst()) c.getString(0)?.substringAfterLast('.', "") else null
                }
            }.getOrNull()
        }
        val cleaned = fromName?.lowercase(Locale.US)?.filter { it.isLetterOrDigit() }
        if (!cleaned.isNullOrBlank() && cleaned.length in 2..5) return cleaned
        return when (track.codec.uppercase(Locale.US)) {
            "FLAC" -> "flac"
            "WAV" -> "wav"
            "AAC" -> "m4a"
            "OPUS" -> "opus"
            "OGG" -> "ogg"
            else -> "mp3"
        }
    }

    private fun resolveDeviceName(): String {
        val model = Build.MODEL?.trim().orEmpty()
        val brand = Build.MANUFACTURER?.trim().orEmpty()
        return when {
            model.isBlank() -> "AudioStudio Device"
            model.startsWith(brand, ignoreCase = true) -> model
            else -> "${brand.replaceFirstChar { it.uppercase() }} $model"
        }
    }

    private fun findLocalNetworkAddresses(): Pair<String?, String?> {
        var fallbackIp: String? = null
        var fallbackBroadcast: String? = null
        runCatching {
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return null to null
            while (interfaces.hasMoreElements()) {
                val nif = interfaces.nextElement()
                if (!nif.isUp || nif.isLoopback || nif.isVirtual) continue
                val name = nif.name.lowercase(Locale.US)
                val preferred = name.startsWith("wlan") || name.startsWith("ap") ||
                    name.startsWith("swlan") || name.startsWith("wifi") || name.startsWith("eth")
                for (ifa in nif.interfaceAddresses) {
                    val addr = ifa.address
                    if (addr is Inet4Address && !addr.isLoopbackAddress && addr.isSiteLocalAddress) {
                        val ip = addr.hostAddress
                        val bcast = ifa.broadcast?.hostAddress
                        if (preferred) return ip to bcast
                        if (fallbackIp == null) {
                            fallbackIp = ip
                            fallbackBroadcast = bcast
                        }
                    }
                }
            }
        }
        return fallbackIp to fallbackBroadcast
    }

    companion object {
        const val DISCOVERY_UDP_PORT = 47821
        const val DEFAULT_TCP_PORT = 47822
        private const val BEACON_MAGIC = "ASP1_BEACON"
        private const val BUFFER_SIZE = 65_536
    }
}
