@file:OptIn(UnstableApi::class)

package com.audiostudiopro.app

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.AuxEffectInfo
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var exoPlayer: ExoPlayer
    private val dspController = HardwareDspController()
    private val stereoProcessor = StereoBalanceAudioProcessor()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private data class HardwareDspSnapshot(
        val eqEnabled: Boolean,
        val bandGainsDb: List<Float>,
        val bassBoostStrength: Int,
        val preampGainDb: Float,
        val virtualizerStrength: Int,
        val reverbPreset: ReverbPreset
    )

    private val playerListener = object : Player.Listener {
        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            if (audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
                val aux = dspController.attachToSession(audioSessionId, StudioEngineBridge.dspState.value)
                syncReverbAuxEffect(aux)
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val activeLoop = StudioEngineBridge.dspState.value.abLoop
            if (activeLoop.trackId != null && activeLoop.trackId != mediaItem?.mediaId) {
                StudioEngineBridge.clearAbLoop()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                val sid = exoPlayer.audioSessionId
                if (sid != C.AUDIO_SESSION_ID_UNSET) {
                    val aux = dspController.attachToSession(sid, StudioEngineBridge.dspState.value)
                    syncReverbAuxEffect(aux)
                }
            } else {
                if (exoPlayer.volume < 0.99f) {
                    exoPlayer.volume = 1.0f
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        val renderersFactory = object : DefaultRenderersFactory(this) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean
            ): AudioSink {
                return DefaultAudioSink.Builder(context)
                    .setEnableFloatOutput(enableFloatOutput)
                    .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                    .setAudioProcessors(arrayOf(stereoProcessor))
                    .build()
            }
        }

        exoPlayer = ExoPlayer.Builder(this, renderersFactory)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus = */ true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()

        exoPlayer.addListener(playerListener)

        if (exoPlayer.audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
            val aux = dspController.attachToSession(
                exoPlayer.audioSessionId,
                StudioEngineBridge.dspState.value
            )
            syncReverbAuxEffect(aux)
        }

        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val sessionActivityIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionActivity(sessionActivityIntent)
            .build()

        observeDspState()
        startRealtimeAudioEngineLoop()
    }

    private fun syncReverbAuxEffect(auxEffectInfo: AuxEffectInfo?) {
        runCatching {
            if (auxEffectInfo != null) {
                exoPlayer.setAuxEffectInfo(auxEffectInfo)
            } else {
                exoPlayer.clearAuxEffectInfo()
            }
        }
    }

    private fun observeDspState() {
        serviceScope.launch {
            var lastSnapshot: HardwareDspSnapshot? = null
            StudioEngineBridge.dspState.collect { state ->
                // 1. Real-time PCM Stereo Balance & Mono Mix (lock-free volatile update)
                stereoProcessor.balance = state.stereoBalance
                stereoProcessor.monoMix = state.monoMix

                // 2. Hardware AudioFX Rack (only dispatch IPC when values actually change)
                val snapshot = HardwareDspSnapshot(
                    eqEnabled = state.eqEnabled,
                    bandGainsDb = state.bandGainsDb,
                    bassBoostStrength = state.bassBoostStrength,
                    preampGainDb = state.preampGainDb,
                    virtualizerStrength = state.virtualizerStrength,
                    reverbPreset = state.reverbPreset
                )
                if (snapshot != lastSnapshot) {
                    lastSnapshot = snapshot
                    val sid = exoPlayer.audioSessionId
                    val aux = if (sid != C.AUDIO_SESSION_ID_UNSET) {
                        dspController.attachToSession(sid, state)
                    } else {
                        dspController.applyState(state)
                    }
                    syncReverbAuxEffect(aux)
                }

                // 3. Varispeed Tempo & Independent Pitch
                val currentParams = exoPlayer.playbackParameters
                if (currentParams.speed != state.speed || currentParams.pitch != state.pitch) {
                    exoPlayer.playbackParameters = PlaybackParameters(state.speed, state.pitch)
                }

                // 4. Smart Silence Skipping
                if (exoPlayer.skipSilenceEnabled != state.skipSilence) {
                    exoPlayer.skipSilenceEnabled = state.skipSilence
                }
            }
        }
    }

    private fun startRealtimeAudioEngineLoop() {
        serviceScope.launch {
            while (isActive) {
                val state = StudioEngineBridge.dspState.value
                var targetVolume = 1.0f

                // 1. A-B Section Looper
                val loop = state.abLoop
                if (exoPlayer.isPlaying && loop.enabled && loop.isReady) {
                    val currentId = exoPlayer.currentMediaItem?.mediaId
                    val pointA = loop.pointAMs ?: 0L
                    val pointB = loop.pointBMs ?: Long.MAX_VALUE
                    if (currentId == loop.trackId && exoPlayer.currentPosition >= pointB) {
                        exoPlayer.seekTo(pointA)
                    }
                }

                // 2. Soft Auto-Fade (Opt-in; defaults to True Gapless at 0s)
                val crossfadeMs = state.crossfadeSeconds * 1000L
                if (exoPlayer.isPlaying && crossfadeMs > 0L) {
                    val duration = exoPlayer.duration
                    val pos = exoPlayer.currentPosition
                    if (duration != C.TIME_UNSET && duration > crossfadeMs * 2 + 2000L) {
                        val remaining = duration - pos
                        val fadeInWindow = (crossfadeMs / 2L).coerceAtLeast(500L)
                        targetVolume = when {
                            remaining in 0L..crossfadeMs ->
                                (remaining.toFloat() / crossfadeMs.toFloat()).coerceIn(0.08f, 1.0f)
                            pos in 0L..fadeInWindow ->
                                (pos.toFloat() / fadeInWindow.toFloat()).coerceIn(0.15f, 1.0f)
                            else -> 1.0f
                        }
                    }
                }

                // 3. Sleep Timer with smooth 15-second fade-out
                val sleepEnd = state.sleepTimerEndEpochMs
                if (sleepEnd != null) {
                    val remaining = sleepEnd - System.currentTimeMillis()
                    if (remaining <= 0L) {
                        exoPlayer.pause()
                        exoPlayer.volume = 1.0f
                        StudioEngineBridge.updateSleepTimerRemaining(0L)
                        targetVolume = 1.0f
                    } else {
                        StudioEngineBridge.updateSleepTimerRemaining(remaining)
                        if (remaining <= 15_000L && exoPlayer.isPlaying) {
                            val sleepFade = (remaining.toFloat() / 15_000f).coerceIn(0.05f, 1.0f)
                            targetVolume = minOf(targetVolume, sleepFade)
                        }
                    }
                }

                if (exoPlayer.isPlaying && abs(exoPlayer.volume - targetVolume) > 0.015f) {
                    exoPlayer.volume = targetVolume
                }

                delay(120L)
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player == null ||
            !player.playWhenReady ||
            player.mediaItemCount == 0 ||
            player.playbackState == Player.STATE_ENDED
        ) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        dspController.release()
        mediaSession?.run {
            player.removeListener(playerListener)
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
