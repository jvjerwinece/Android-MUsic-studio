@file:OptIn(UnstableApi::class)

package com.audiostudiopro.app

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

data class PlaybackUiState(
    val currentTrack: AudioTrack? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val shuffleEnabled: Boolean = false,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val queue: List<AudioTrack> = emptyList(),
    val currentQueueIndex: Int = -1
)

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    private val store = LocalLibraryStore(appContext)
    private val beamEngine = StudioBeamEngine(appContext, viewModelScope)

    private val _tracks = MutableStateFlow<List<AudioTrack>>(emptyList())
    val tracks: StateFlow<List<AudioTrack>> = _tracks.asStateFlow()

    private val _playlists = MutableStateFlow<List<UserPlaylist>>(emptyList())
    val playlists: StateFlow<List<UserPlaylist>> = _playlists.asStateFlow()

    private val _customEqPresets = MutableStateFlow<List<CustomEqPreset>>(emptyList())
    val customEqPresets: StateFlow<List<CustomEqPreset>> = _customEqPresets.asStateFlow()

    private val _likedIds = MutableStateFlow<Set<String>>(emptySet())
    val likedIds: StateFlow<Set<String>> = _likedIds.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(LibrarySort.RECENT)
    val sortOrder: StateFlow<LibrarySort> = _sortOrder.asStateFlow()

    private val _filterMode = MutableStateFlow(LibraryFilter.ALL)
    val filterMode: StateFlow<LibraryFilter> = _filterMode.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackUiState())
    val playbackState: StateFlow<PlaybackUiState> = _playbackState.asStateFlow()

    private val _ambientPalette = MutableStateFlow(AmbientPalette.Default)
    val ambientPalette: StateFlow<AmbientPalette> = _ambientPalette.asStateFlow()

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    val dspState: StateFlow<DspState> = StudioEngineBridge.dspState
    val beamState: StateFlow<StudioBeamUiState> = beamEngine.state

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var lastRecordedPlayTrackId: String? = null

    private val controllerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            syncFromController()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            syncFromController()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            syncFromController()
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            syncFromController()
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            syncFromController()
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            syncFromController()
        }
    }

    init {
        _tracks.value = store.loadTracks()
        _playlists.value = store.loadPlaylists()
        _likedIds.value = store.loadLikedIds()
        _customEqPresets.value = store.loadCustomEqPresets()

        connectToPlaybackService()
        startProgressTicker()
    }

    private fun connectToPlaybackService() {
        val token = SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java))
        val future = MediaController.Builder(appContext, token).buildAsync()
        controllerFuture = future

        future.addListener(
            {
                runCatching {
                    val controller = future.get()
                    mediaController = controller
                    controller.addListener(controllerListener)
                    syncFromController()
                }
            },
            ContextCompat.getMainExecutor(appContext)
        )
    }

    private fun startProgressTicker() {
        viewModelScope.launch {
            while (isActive) {
                val controller = mediaController
                if (controller != null) {
                    val duration = controller.duration.takeIf { it > 0L }
                        ?: _playbackState.value.currentTrack?.durationMs
                        ?: 0L
                    val pos = controller.currentPosition.coerceAtLeast(0L)
                    _playbackState.update {
                        if (it.positionMs != pos || it.durationMs != duration || it.isPlaying != controller.isPlaying) {
                            it.copy(
                                positionMs = pos,
                                durationMs = duration,
                                isPlaying = controller.isPlaying
                            )
                        } else {
                            it
                        }
                    }
                }
                delay(200L)
            }
        }
    }

    private fun syncFromController() {
        val controller = mediaController ?: return
        val libraryById = _tracks.value.associateBy { it.id }

        val queueList = buildList(controller.mediaItemCount) {
            for (i in 0 until controller.mediaItemCount) {
                val item = controller.getMediaItemAt(i)
                val resolved = libraryById[item.mediaId] ?: AudioTrack(
                    id = item.mediaId.ifBlank { "ext_$i" },
                    uriString = item.localConfiguration?.uri?.toString() ?: "",
                    title = item.mediaMetadata.title?.toString() ?: "Unknown Track",
                    artist = item.mediaMetadata.artist?.toString() ?: "Unknown Artist",
                    album = item.mediaMetadata.albumTitle?.toString() ?: "Local Library",
                    durationMs = 0L
                )
                add(resolved)
            }
        }

        val currentIndex = controller.currentMediaItemIndex.takeIf { it in queueList.indices } ?: -1
        val currentTrack = queueList.getOrNull(currentIndex)
        val duration = controller.duration.takeIf { it > 0L } ?: currentTrack?.durationMs ?: 0L

        val previousTrackId = _playbackState.value.currentTrack?.id
        _playbackState.value = PlaybackUiState(
            currentTrack = currentTrack,
            isPlaying = controller.isPlaying,
            positionMs = controller.currentPosition.coerceAtLeast(0L),
            durationMs = duration,
            shuffleEnabled = controller.shuffleModeEnabled,
            repeatMode = controller.repeatMode,
            queue = queueList,
            currentQueueIndex = currentIndex
        )

        if (currentTrack != null && controller.isPlaying && currentTrack.id != lastRecordedPlayTrackId) {
            lastRecordedPlayTrackId = currentTrack.id
            recordTrackPlay(currentTrack.id)
        }

        if (currentTrack?.id != previousTrackId) {
            refreshAmbientPalette(currentTrack)
        }
    }

    private fun recordTrackPlay(trackId: String) {
        val now = System.currentTimeMillis()
        var changed = false
        val updated = _tracks.value.map { t ->
            if (t.id == trackId) {
                changed = true
                t.copy(playCount = t.playCount + 1, lastPlayedAt = now)
            } else {
                t
            }
        }
        if (changed) {
            _tracks.value = updated
            store.saveTracks(updated)
        }
    }

    private fun refreshAmbientPalette(track: AudioTrack?) {
        viewModelScope.launch {
            _ambientPalette.value = ArtworkCache.extractPalette(track)
        }
    }

    fun importAudioUris(uris: List<Uri>) {
        if (uris.isEmpty()) return
        viewModelScope.launch {
            _isImporting.value = true
            val existingUris = _tracks.value.map { it.uriString }.toMutableSet()
            val newlyImported = mutableListOf<AudioTrack>()

            for (uri in uris) {
                runCatching {
                    appContext.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                if (existingUris.add(uri.toString())) {
                    val track = MetadataExtractor.extractTrack(appContext, uri)
                    newlyImported.add(track)
                }
            }

            if (newlyImported.isNotEmpty()) {
                val updated = newlyImported + _tracks.value
                _tracks.value = updated
                store.saveTracks(updated)
                showBanner("Imported ${newlyImported.size} track${if (newlyImported.size == 1) "" else "s"}")
            } else {
                showBanner("Selected tracks are already in your library")
            }
            _isImporting.value = false
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(sort: LibrarySort) {
        _sortOrder.value = sort
    }

    fun setFilterMode(filter: LibraryFilter) {
        _filterMode.value = filter
    }

    fun playTracks(trackList: List<AudioTrack>, startIndex: Int = 0, shuffle: Boolean = false) {
        val controller = mediaController ?: return
        if (trackList.isEmpty()) return

        val actualStart = if (shuffle) {
            trackList.indices.random()
        } else {
            startIndex.coerceIn(0, trackList.lastIndex)
        }
        val mediaItems = trackList.map { it.toMediaItem() }

        controller.shuffleModeEnabled = shuffle
        controller.setMediaItems(mediaItems, actualStart, 0L)
        controller.prepare()
        controller.play()
        syncFromController()
    }

    fun playNext(track: AudioTrack) {
        val controller = mediaController ?: return
        if (controller.mediaItemCount == 0) {
            playTracks(listOf(track), 0, false)
            showBanner("Playing ${track.title}")
            return
        }
        val insertIndex = (controller.currentMediaItemIndex + 1).coerceAtMost(controller.mediaItemCount)
        controller.addMediaItem(insertIndex, track.toMediaItem())
        syncFromController()
        showBanner("Queued \"${track.title}\" next")
    }

    fun queueBatchNext(selectedTracks: List<AudioTrack>) {
        if (selectedTracks.isEmpty()) return
        val controller = mediaController ?: return
        if (controller.mediaItemCount == 0) {
            playTracks(selectedTracks, 0, false)
            showBanner("Playing ${selectedTracks.size} tracks")
            return
        }
        val insertIndex = (controller.currentMediaItemIndex + 1).coerceAtMost(controller.mediaItemCount)
        controller.addMediaItems(insertIndex, selectedTracks.map { it.toMediaItem() })
        syncFromController()
        showBanner("Queued ${selectedTracks.size} tracks next")
    }

    fun jumpToQueueIndex(index: Int) {
        val controller = mediaController ?: return
        if (index in 0 until controller.mediaItemCount) {
            controller.seekToDefaultPosition(index)
            controller.play()
            syncFromController()
        }
    }

    fun removeFromQueue(index: Int) {
        val controller = mediaController ?: return
        if (index in 0 until controller.mediaItemCount) {
            controller.removeMediaItem(index)
            syncFromController()
        }
    }

    fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.mediaItemCount == 0 && _tracks.value.isNotEmpty()) {
            playTracks(_tracks.value, 0, false)
            return
        }
        if (controller.isPlaying) {
            controller.pause()
        } else {
            if (controller.playbackState == Player.STATE_ENDED) {
                controller.seekTo(0L)
            }
            controller.play()
        }
        syncFromController()
    }

    fun skipNext() {
        val controller = mediaController ?: return
        if (controller.hasNextMediaItem()) {
            controller.seekToNextMediaItem()
        } else if (controller.mediaItemCount > 0) {
            controller.seekTo(0, 0L)
        }
        controller.play()
    }

    fun skipPrevious() {
        val controller = mediaController ?: return
        if (controller.currentPosition > 3500L) {
            controller.seekTo(0L)
        } else if (controller.hasPreviousMediaItem()) {
            controller.seekToPreviousMediaItem()
        } else {
            controller.seekTo(0L)
        }
    }

    fun seekTo(positionMs: Long) {
        val controller = mediaController ?: return
        val maxDur = _playbackState.value.durationMs.takeIf { it > 0L } ?: Long.MAX_VALUE
        val target = positionMs.coerceIn(0L, maxDur)
        controller.seekTo(target)
        _playbackState.update { it.copy(positionMs = target) }
    }

    fun nudgePosition(deltaMs: Long) {
        seekTo(_playbackState.value.positionMs + deltaMs)
    }

    fun toggleShuffle() {
        val controller = mediaController ?: return
        controller.shuffleModeEnabled = !controller.shuffleModeEnabled
        syncFromController()
    }

    fun cycleRepeatMode() {
        val controller = mediaController ?: return
        controller.repeatMode = when (controller.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        syncFromController()
    }

    fun toggleLike(trackId: String) {
        val updated = _likedIds.value.toMutableSet()
        if (!updated.add(trackId)) {
            updated.remove(trackId)
        }
        _likedIds.value = updated
        store.saveLikedIds(updated)
    }

    fun updateTrackMetadata(trackId: String, newTitle: String, newArtist: String, newAlbum: String) {
        val cleanTitle = newTitle.trim()
        if (cleanTitle.isEmpty()) return
        val cleanArtist = newArtist.trim().ifBlank { "Unknown Artist" }
        val cleanAlbum = newAlbum.trim().ifBlank { "Local Library" }

        val updated = _tracks.value.map { t ->
            if (t.id == trackId) {
                t.copy(title = cleanTitle, artist = cleanArtist, album = cleanAlbum)
            } else {
                t
            }
        }
        _tracks.value = updated
        store.saveTracks(updated)
        syncFromController()
        showBanner("Updated track info")
    }

    fun removeTrackFromLibrary(track: AudioTrack) {
        val updatedTracks = _tracks.value.filterNot { it.id == track.id }
        _tracks.value = updatedTracks
        store.saveTracks(updatedTracks)

        val updatedLiked = _likedIds.value - track.id
        _likedIds.value = updatedLiked
        store.saveLikedIds(updatedLiked)

        val updatedPlaylists = _playlists.value.map { playlist ->
            playlist.copy(trackIds = playlist.trackIds.filterNot { it == track.id })
        }
        _playlists.value = updatedPlaylists
        store.savePlaylists(updatedPlaylists)

        mediaController?.let { controller ->
            for (i in controller.mediaItemCount - 1 downTo 0) {
                if (controller.getMediaItemAt(i).mediaId == track.id) {
                    controller.removeMediaItem(i)
                }
            }
            syncFromController()
        }

        track.artworkPath?.let { path -> runCatching { File(path).delete() } }
        val uri = Uri.parse(track.uriString)
        if (uri.scheme == "file" && uri.path?.contains("received_music") == true) {
            runCatching { File(uri.path!!).delete() }
        }
        showBanner("Removed \"${track.title}\"")
    }

    fun createPlaylist(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val newPlaylist = UserPlaylist(name = trimmed)
        val updated = listOf(newPlaylist) + _playlists.value
        _playlists.value = updated
        store.savePlaylists(updated)
        showBanner("Created playlist \"$trimmed\"")
    }

    fun deletePlaylist(playlistId: String) {
        val updated = _playlists.value.filterNot { it.id == playlistId }
        _playlists.value = updated
        store.savePlaylists(updated)
    }

    fun addTrackToPlaylist(playlistId: String, trackId: String) {
        val updated = _playlists.value.map { playlist ->
            if (playlist.id == playlistId && !playlist.trackIds.contains(trackId)) {
                playlist.copy(trackIds = playlist.trackIds + trackId)
            } else {
                playlist
            }
        }
        _playlists.value = updated
        store.savePlaylists(updated)
        val targetName = updated.firstOrNull { it.id == playlistId }?.name ?: "Playlist"
        showBanner("Added to $targetName")
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        val updated = _playlists.value.map { playlist ->
            if (playlist.id == playlistId) {
                playlist.copy(trackIds = playlist.trackIds.filterNot { it == trackId })
            } else {
                playlist
            }
        }
        _playlists.value = updated
        store.savePlaylists(updated)
    }

    fun moveTrackInPlaylist(playlistId: String, fromIndex: Int, toIndex: Int) {
        val updated = _playlists.value.map { playlist ->
            if (playlist.id == playlistId &&
                fromIndex in playlist.trackIds.indices &&
                toIndex in playlist.trackIds.indices
            ) {
                val mutable = playlist.trackIds.toMutableList()
                val moved = mutable.removeAt(fromIndex)
                mutable.add(toIndex, moved)
                playlist.copy(trackIds = mutable)
            } else {
                playlist
            }
        }
        _playlists.value = updated
        store.savePlaylists(updated)
    }

    // Custom Saved EQ Presets
    fun saveCurrentEqAsCustomPreset(name: String) {
        val clean = name.trim()
        if (clean.isEmpty()) return
        val current = dspState.value
        val preset = CustomEqPreset(
            name = clean,
            bandGainsDb = current.bandGainsDb.toList(),
            bassStrength = current.bassBoostStrength
        )
        val updated = listOf(preset) + _customEqPresets.value
        _customEqPresets.value = updated
        store.saveCustomEqPresets(updated)
        StudioEngineBridge.applyCustomPreset(preset)
        showBanner("Saved EQ preset \"$clean\"")
    }

    fun applyCustomEqPreset(preset: CustomEqPreset) {
        StudioEngineBridge.applyCustomPreset(preset)
    }

    fun deleteCustomEqPreset(presetId: String) {
        val updated = _customEqPresets.value.filterNot { it.id == presetId }
        _customEqPresets.value = updated
        store.saveCustomEqPresets(updated)
    }

    // StudioBeam (ASP1) Controls
    fun setBeamRequirePin(requirePin: Boolean) = beamEngine.setRequirePin(requirePin)

    fun startBeamSend(selectedIds: Set<String>) {
        val toSend = _tracks.value.filter { selectedIds.contains(it.id) }
        beamEngine.startSending(toSend)
    }

    fun startBeamScanning() = beamEngine.startScanningForPeers()
    fun stopBeamScanning() = beamEngine.stopScanning()
    fun stopBeamSession() = beamEngine.stopAll()

    fun connectBeamPeer(peer: DiscoveredBeamPeer, pin: String) {
        beamEngine.receiveFromPeer(
            hostIp = peer.hostIp,
            port = peer.port,
            pin = pin,
            expectedTotalBytes = peer.totalBytes
        ) { receivedTrack ->
            addReceivedTrackToLibrary(receivedTrack)
        }
    }

    fun connectBeamManual(codeOrAddress: String, pin: String): Boolean {
        val decoded = BeamCodeCodec.decode(codeOrAddress) ?: return false
        beamEngine.receiveFromPeer(
            hostIp = decoded.first,
            port = decoded.second,
            pin = pin
        ) { receivedTrack ->
            addReceivedTrackToLibrary(receivedTrack)
        }
        return true
    }

    private fun addReceivedTrackToLibrary(track: AudioTrack) {
        val updated = listOf(track) + _tracks.value
        _tracks.value = updated
        store.saveTracks(updated)
    }

    // DSP Controls
    fun setEqEnabled(enabled: Boolean) = StudioEngineBridge.setEqEnabled(enabled)
    fun applyEqPreset(preset: EqPreset) = StudioEngineBridge.applyPreset(preset)
    fun setEqBandGain(index: Int, gainDb: Float) = StudioEngineBridge.setBandGain(index, gainDb)
    fun setBassBoost(strength: Int) = StudioEngineBridge.setBassBoost(strength)
    fun setPreampGainDb(gainDb: Float) = StudioEngineBridge.setPreampGainDb(gainDb)
    fun setVirtualizerStrength(strength: Int) = StudioEngineBridge.setVirtualizerStrength(strength)
    fun setStereoBalance(balance: Float) = StudioEngineBridge.setStereoBalance(balance)
    fun setMonoMix(enabled: Boolean) = StudioEngineBridge.setMonoMix(enabled)
    fun setReverbPreset(preset: ReverbPreset) = StudioEngineBridge.setReverbPreset(preset)
    fun resetStageAndRoom() = StudioEngineBridge.resetStageAndRoom()
    fun setSpeed(speed: Float) = StudioEngineBridge.setSpeed(speed)
    fun setPitch(pitch: Float) = StudioEngineBridge.setPitch(pitch)
    fun setSkipSilence(enabled: Boolean) = StudioEngineBridge.setSkipSilence(enabled)
    fun resetTempoPitch() = StudioEngineBridge.resetTempoPitch()
    fun setCrossfadeSeconds(seconds: Int) = StudioEngineBridge.setCrossfadeSeconds(seconds)
    fun setSleepTimerMinutes(minutes: Int) = StudioEngineBridge.setSleepTimerMinutes(minutes)

    fun markLoopA() {
        val state = _playbackState.value
        val trackId = state.currentTrack?.id ?: return
        StudioEngineBridge.setLoopPointA(trackId, state.positionMs)
    }

    fun markLoopB() {
        val state = _playbackState.value
        val trackId = state.currentTrack?.id ?: return
        StudioEngineBridge.setLoopPointB(trackId, state.positionMs)
    }

    fun toggleAbLoop(enabled: Boolean) = StudioEngineBridge.toggleAbLoop(enabled)
    fun clearAbLoop() = StudioEngineBridge.clearAbLoop()

    fun showBanner(message: String) {
        _statusBanner.value = message
        viewModelScope.launch {
            delay(2400L)
            if (_statusBanner.value == message) {
                _statusBanner.value = null
            }
        }
    }

    override fun onCleared() {
        beamEngine.stopAll()
        mediaController?.removeListener(controllerListener)
        controllerFuture?.let { MediaController.releaseFuture(it) }
        super.onCleared()
    }
}
