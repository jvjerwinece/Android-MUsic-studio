package com.audiostudiopro.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.net.Uri
import android.provider.OpenableColumns
import android.util.LruCache
import androidx.compose.ui.graphics.Color
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.roundToInt

data class AudioTrack(
    val id: String,
    val uriString: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val artworkPath: String? = null,
    val dateAdded: Long = System.currentTimeMillis()
) {
    fun toMediaItem(): MediaItem {
        val metadataBuilder = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setAlbumTitle(album)

        if (!artworkPath.isNullOrBlank()) {
            val file = File(artworkPath)
            if (file.exists()) {
                metadataBuilder.setArtworkUri(Uri.fromFile(file))
            }
        }

        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(Uri.parse(uriString))
            .setMediaMetadata(metadataBuilder.build())
            .build()
    }
}

data class UserPlaylist(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val trackIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class AmbientPalette(
    val primary: Color,
    val secondary: Color,
    val accent: Color
) {
    companion object {
        private val curatedFallbacks = listOf(
            AmbientPalette(Color(0xFF4F46E5), Color(0xFF1E1B4B), Color(0xFF818CF8)),
            AmbientPalette(Color(0xFF0EA5E9), Color(0xFF0C4A6E), Color(0xFF38BDF8)),
            AmbientPalette(Color(0xFFE11D48), Color(0xFF4C0519), Color(0xFFFB7185)),
            AmbientPalette(Color(0xFFD97706), Color(0xFF451A03), Color(0xFFFBBF24)),
            AmbientPalette(Color(0xFF10B981), Color(0xFF064E3B), Color(0xFF34D399)),
            AmbientPalette(Color(0xFF9333EA), Color(0xFF3B0764), Color(0xFFC084FC))
        )

        val Default = curatedFallbacks.first()

        fun fallbackFor(seed: String?): AmbientPalette {
            if (seed.isNullOrBlank()) return Default
            val index = abs(seed.hashCode()) % curatedFallbacks.size
            return curatedFallbacks[index]
        }
    }
}

enum class EqPreset(val label: String, val bandsDb: List<Float>, val bassStrength: Int) {
    FLAT("Flat", listOf(0f, 0f, 0f, 0f, 0f), 0),
    BASS_BOOST("Bass Boost", listOf(7.5f, 4.5f, 0.5f, 1.0f, 2.0f), 650),
    VOCAL_CLARITY("Vocal Clarity", listOf(-2.0f, 0.5f, 4.5f, 5.0f, 2.5f), 150),
    LATE_NIGHT("Late Night", listOf(3.5f, 2.0f, -1.5f, 1.5f, -1.0f), 300),
    STUDIO_AIR("Studio Air", listOf(2.0f, 0.5f, 0.0f, 2.5f, 6.0f), 200),
    CUSTOM("Custom", listOf(0f, 0f, 0f, 0f, 0f), 0)
}

data class AbLoopState(
    val trackId: String? = null,
    val pointAMs: Long? = null,
    val pointBMs: Long? = null,
    val enabled: Boolean = false
) {
    val isReady: Boolean
        get() = pointAMs != null && pointBMs != null && pointBMs > pointAMs
}

data class DspState(
    val eqEnabled: Boolean = false,
    val activePreset: EqPreset = EqPreset.FLAT,
    val bandGainsDb: List<Float> = listOf(0f, 0f, 0f, 0f, 0f),
    val bassBoostStrength: Int = 0,
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f,
    val crossfadeSeconds: Int = 0,
    val abLoop: AbLoopState = AbLoopState(),
    val sleepTimerSelectedMinutes: Int = 0,
    val sleepTimerEndEpochMs: Long? = null,
    val sleepTimerRemainingMs: Long = 0L
) {
    val hasActiveProcessing: Boolean
        get() = eqEnabled ||
            abs(speed - 1.0f) > 0.01f ||
            abs(pitch - 1.0f) > 0.01f ||
            crossfadeSeconds > 0 ||
            abLoop.enabled ||
            sleepTimerEndEpochMs != null
}

/**
 * Process-wide reactive bridge shared between PlaybackService and MusicViewModel.
 * Ensures all DSP, Looper, Sleep Timer, and Crossfade controls continue executing
 * cleanly in the foreground service even when the user swipes the UI away from Recents.
 */
object StudioEngineBridge {
    val bandLabels = listOf("60Hz", "230Hz", "910Hz", "3.6kHz", "14kHz")

    private val _dspState = MutableStateFlow(DspState())
    val dspState: StateFlow<DspState> = _dspState.asStateFlow()

    fun applyPreset(preset: EqPreset) {
        _dspState.update { current ->
            current.copy(
                eqEnabled = true,
                activePreset = preset,
                bandGainsDb = preset.bandsDb,
                bassBoostStrength = preset.bassStrength
            )
        }
    }

    fun setBandGain(bandIndex: Int, gainDb: Float) {
        if (bandIndex !in 0..4) return
        val clamped = gainDb.coerceIn(-12f, 12f)
        _dspState.update { current ->
            val updated = current.bandGainsDb.toMutableList()
            updated[bandIndex] = clamped
            current.copy(
                eqEnabled = true,
                activePreset = EqPreset.CUSTOM,
                bandGainsDb = updated
            )
        }
    }

    fun setBassBoost(strength: Int) {
        _dspState.update { current ->
            current.copy(
                eqEnabled = true,
                activePreset = EqPreset.CUSTOM,
                bassBoostStrength = strength.coerceIn(0, 1000)
            )
        }
    }

    fun setEqEnabled(enabled: Boolean) {
        _dspState.update { it.copy(eqEnabled = enabled) }
    }

    fun setSpeed(speed: Float) {
        _dspState.update { it.copy(speed = speed.coerceIn(0.50f, 2.00f)) }
    }

    fun setPitch(pitch: Float) {
        _dspState.update { it.copy(pitch = pitch.coerceIn(0.50f, 2.00f)) }
    }

    fun resetTempoPitch() {
        _dspState.update { it.copy(speed = 1.0f, pitch = 1.0f) }
    }

    fun setCrossfadeSeconds(seconds: Int) {
        _dspState.update { it.copy(crossfadeSeconds = seconds.coerceIn(0, 8)) }
    }

    fun setLoopPointA(trackId: String, positionMs: Long) {
        _dspState.update { current ->
            val existingB = if (current.abLoop.trackId == trackId) current.abLoop.pointBMs else null
            val validB = if (existingB != null && existingB >= positionMs + 500L) existingB else null
            current.copy(
                abLoop = AbLoopState(
                    trackId = trackId,
                    pointAMs = positionMs,
                    pointBMs = validB,
                    enabled = validB != null
                )
            )
        }
    }

    fun setLoopPointB(trackId: String, positionMs: Long) {
        _dspState.update { current ->
            val existingA = if (current.abLoop.trackId == trackId) current.abLoop.pointAMs else 0L
            val safeA = (existingA ?: 0L).coerceAtMost((positionMs - 500L).coerceAtLeast(0L))
            current.copy(
                abLoop = AbLoopState(
                    trackId = trackId,
                    pointAMs = safeA,
                    pointBMs = positionMs,
                    enabled = positionMs >= safeA + 500L
                )
            )
        }
    }

    fun toggleAbLoop(enabled: Boolean) {
        _dspState.update { current ->
            if (!current.abLoop.isReady) current
            else current.copy(abLoop = current.abLoop.copy(enabled = enabled))
        }
    }

    fun clearAbLoop() {
        _dspState.update { it.copy(abLoop = AbLoopState()) }
    }

    fun setSleepTimerMinutes(minutes: Int) {
        if (minutes <= 0) {
            _dspState.update {
                it.copy(
                    sleepTimerSelectedMinutes = 0,
                    sleepTimerEndEpochMs = null,
                    sleepTimerRemainingMs = 0L
                )
            }
        } else {
            val durationMs = minutes * 60_000L
            val endEpoch = System.currentTimeMillis() + durationMs
            _dspState.update {
                it.copy(
                    sleepTimerSelectedMinutes = minutes,
                    sleepTimerEndEpochMs = endEpoch,
                    sleepTimerRemainingMs = durationMs
                )
            }
        }
    }

    fun updateSleepTimerRemaining(remainingMs: Long) {
        _dspState.update { current ->
            if (remainingMs <= 0L) {
                current.copy(
                    sleepTimerSelectedMinutes = 0,
                    sleepTimerEndEpochMs = null,
                    sleepTimerRemainingMs = 0L
                )
            } else if ((current.sleepTimerRemainingMs / 1000L) != (remainingMs / 1000L)) {
                current.copy(sleepTimerRemainingMs = remainingMs)
            } else {
                current
            }
        }
    }
}

/**
 * Hardware 5-Band Graphic Equalizer + BassBoost attached to ExoPlayer's audioSessionId.
 * Gracefully adapts to any Android OEM's hardware band count and millibel ceiling.
 */
class HardwareDspController {
    private var attachedSessionId: Int = 0
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null

    fun attachToSession(audioSessionId: Int, currentState: DspState) {
        if (audioSessionId <= 0) return
        if (audioSessionId == attachedSessionId && equalizer != null) {
            applyState(currentState)
            return
        }

        release()
        attachedSessionId = audioSessionId

        runCatching {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = currentState.eqEnabled
            }
        }

        runCatching {
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = currentState.eqEnabled && currentState.bassBoostStrength > 0
            }
        }

        applyState(currentState)
    }

    fun applyState(state: DspState) {
        equalizer?.let { eq ->
            runCatching {
                eq.enabled = state.eqEnabled
                val hardwareBands = eq.numberOfBands.toInt()
                val range = eq.bandLevelRange
                val minMb = range[0].toInt()
                val maxMb = range[1].toInt()

                if (hardwareBands > 0) {
                    for (hwBand in 0 until hardwareBands) {
                        val mappedUiIndex = if (hardwareBands == 1) {
                            0
                        } else {
                            ((hwBand.toFloat() / (hardwareBands - 1)) * 4f).roundToInt().coerceIn(0, 4)
                        }
                        val targetMb = (state.bandGainsDb[mappedUiIndex] * 100f)
                            .roundToInt()
                            .coerceIn(minMb, maxMb)
                            .toShort()
                        eq.setBandLevel(hwBand.toShort(), targetMb)
                    }
                }
            }
        }

        bassBoost?.let { bb ->
            runCatching {
                val active = state.eqEnabled && state.bassBoostStrength > 0
                bb.enabled = active
                if (bb.strengthSupported) {
                    bb.setStrength(state.bassBoostStrength.coerceIn(0, 1000).toShort())
                }
            }
        }
    }

    fun release() {
        runCatching { equalizer?.release() }
        runCatching { bassBoost?.release() }
        equalizer = null
        bassBoost = null
        attachedSessionId = 0
    }
}

object ArtworkCache {
    private val maxMemoryKb = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSizeKb = (maxMemoryKb / 10).coerceAtLeast(4096)

    private val bitmapCache = object : LruCache<String, Bitmap>(cacheSizeKb) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
    }

    private val paletteCache = ConcurrentHashMap<String, AmbientPalette>()

    suspend fun loadBitmap(path: String?): Bitmap? {
        if (path.isNullOrBlank()) return null
        bitmapCache.get(path)?.let { return it }
        return withContext(Dispatchers.IO) {
            val file = File(path)
            if (!file.exists()) return@withContext null
            val decoded = runCatching { BitmapFactory.decodeFile(file.absolutePath) }.getOrNull()
            if (decoded != null) {
                bitmapCache.put(path, decoded)
            }
            decoded
        }
    }

    suspend fun extractPalette(track: AudioTrack?): AmbientPalette {
        if (track == null) return AmbientPalette.Default
        val key = track.artworkPath ?: track.id
        paletteCache[key]?.let { return it }

        val bitmap = loadBitmap(track.artworkPath)
            ?: return AmbientPalette.fallbackFor(track.id).also { paletteCache[key] = it }

        return withContext(Dispatchers.Default) {
            val palette = Palette.from(bitmap).generate()
            val fallback = AmbientPalette.fallbackFor(track.id)

            val primaryInt = palette.getVibrantColor(
                palette.getLightVibrantColor(
                    palette.getMutedColor(0)
                )
            )
            val secondaryInt = palette.getDarkVibrantColor(
                palette.getDarkMutedColor(0)
            )
            val accentInt = palette.getLightVibrantColor(
                palette.getVibrantColor(0)
            )

            val resolved = AmbientPalette(
                primary = if (primaryInt != 0) Color(primaryInt) else fallback.primary,
                secondary = if (secondaryInt != 0) Color(secondaryInt) else fallback.secondary,
                accent = if (accentInt != 0) Color(accentInt) else fallback.accent
            )
            paletteCache[key] = resolved
            resolved
        }
    }
}

object MetadataExtractor {

    suspend fun extractTrack(context: Context, uri: Uri): AudioTrack = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val fallbackName = queryDisplayName(context, uri)
            ?.substringBeforeLast('.')
            ?.ifBlank { null }
            ?: "Unknown Track"

        var title = fallbackName
        var artist = "Unknown Artist"
        var album = "Local Studio"
        var durationMs = 0L
        var artworkPath: String? = null

        val retriever = MediaMetadataRetriever()
        runCatching {
            retriever.setDataSource(context, uri)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                ?.trim()?.takeIf { it.isNotEmpty() }?.let { title = it }
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                ?.trim()?.takeIf { it.isNotEmpty() }?.let { artist = it }
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                ?.trim()?.takeIf { it.isNotEmpty() }?.let { album = it }
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()?.let { durationMs = it.coerceAtLeast(0L) }

            val embeddedArt = retriever.embeddedPicture
            if (embeddedArt != null && embeddedArt.isNotEmpty()) {
                artworkPath = saveCompressedArtwork(context, id, embeddedArt)
            }
        }
        runCatching { retriever.release() }

        AudioTrack(
            id = id,
            uriString = uri.toString(),
            title = title,
            artist = artist,
            album = album,
            durationMs = durationMs,
            artworkPath = artworkPath
        )
    }

    private fun queryDisplayName(context: Context, uri: Uri): String? {
        return runCatching {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) cursor.getString(idx) else null
                } else null
            }
        }.getOrNull()
    }

    private fun saveCompressedArtwork(context: Context, trackId: String, rawBytes: ByteArray): String? {
        return runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size, bounds)

            var sampleSize = 1
            val targetSize = 512
            while (bounds.outWidth / sampleSize > targetSize * 2 &&
                bounds.outHeight / sampleSize > targetSize * 2
            ) {
                sampleSize *= 2
            }

            val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            val bitmap = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size, decodeOpts)
                ?: return null

            val artDir = File(context.filesDir, "artwork").apply { if (!exists()) mkdirs() }
            val outFile = File(artDir, "$trackId.jpg")
            FileOutputStream(outFile).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 88, stream)
            }
            bitmap.recycle()
            outFile.absolutePath
        }.getOrNull()
    }
}

class LocalLibraryStore(context: Context) {
    private val prefs = context.getSharedPreferences("audiostudio_pro_store_v1", Context.MODE_PRIVATE)

    fun loadTracks(): List<AudioTrack> {
        val raw = prefs.getString(KEY_TRACKS, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            buildList(arr.length()) {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        AudioTrack(
                            id = o.getString("id"),
                            uriString = o.getString("uri"),
                            title = o.getString("title"),
                            artist = o.getString("artist"),
                            album = o.optString("album", "Local Studio"),
                            durationMs = o.optLong("durationMs", 0L),
                            artworkPath = o.optString("artworkPath", "").ifBlank { null },
                            dateAdded = o.optLong("dateAdded", 0L)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun saveTracks(tracks: List<AudioTrack>) {
        val arr = JSONArray()
        tracks.forEach { t ->
            arr.put(
                JSONObject().apply {
                    put("id", t.id)
                    put("uri", t.uriString)
                    put("title", t.title)
                    put("artist", t.artist)
                    put("album", t.album)
                    put("durationMs", t.durationMs)
                    put("artworkPath", t.artworkPath ?: "")
                    put("dateAdded", t.dateAdded)
                }
            )
        }
        prefs.edit().putString(KEY_TRACKS, arr.toString()).apply()
    }

    fun loadPlaylists(): List<UserPlaylist> {
        val raw = prefs.getString(KEY_PLAYLISTS, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            buildList(arr.length()) {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val idArr = o.optJSONArray("trackIds") ?: JSONArray()
                    val ids = buildList(idArr.length()) {
                        for (j in 0 until idArr.length()) add(idArr.getString(j))
                    }
                    add(
                        UserPlaylist(
                            id = o.getString("id"),
                            name = o.getString("name"),
                            trackIds = ids,
                            createdAt = o.optLong("createdAt", 0L)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun savePlaylists(playlists: List<UserPlaylist>) {
        val arr = JSONArray()
        playlists.forEach { p ->
            arr.put(
                JSONObject().apply {
                    put("id", p.id)
                    put("name", p.name)
                    put("createdAt", p.createdAt)
                    put("trackIds", JSONArray(p.trackIds))
                }
            )
        }
        prefs.edit().putString(KEY_PLAYLISTS, arr.toString()).apply()
    }

    fun loadLikedIds(): Set<String> {
        return prefs.getStringSet(KEY_LIKED_IDS, emptySet())?.toSet() ?: emptySet()
    }

    fun saveLikedIds(ids: Set<String>) {
        prefs.edit().putStringSet(KEY_LIKED_IDS, ids).apply()
    }

    companion object {
        private const val KEY_TRACKS = "tracks_json"
        private const val KEY_PLAYLISTS = "playlists_json"
        private const val KEY_LIKED_IDS = "liked_ids_set"
    }
}

fun formatDuration(ms: Long): String {
    if (ms <= 0L) return "0:00"
    val totalSeconds = ms / 1000L
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%d:%02d", minutes, seconds)
    }
}
