@file:OptIn(ExperimentalMaterial3Api::class)

package com.audiostudiopro.app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.QueuePlayNext
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.Player
import java.util.Locale
import kotlin.math.roundToInt

private val ObsidianBg = Color(0xFF08080A)
private val GlassSurfaceLow = Color.White.copy(alpha = 0.05f)
private val GlassSurfaceMid = Color.White.copy(alpha = 0.08f)
private val GlassBorder = Color.White.copy(alpha = 0.12f)
private val TextPrimary = Color(0xFFF5F5F7)
private val TextSecondary = Color(0xFF9A9AA3)
private val TextTertiary = Color(0xFF63636B)

private enum class LibraryTab(val label: String) {
    LIBRARY("Library"),
    PLAYLISTS("Playlists"),
    LIKED("Favorites")
}

private enum class AudioLabTab(val label: String, val icon: ImageVector) {
    EQ("EQ & Bass", Icons.Rounded.GraphicEq),
    SPEED("Speed & Pitch", Icons.Rounded.Speed),
    FLOW("Loop & Flow", Icons.Rounded.Loop)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )

        setContent {
            val colorScheme = darkColorScheme(
                primary = Color(0xFFF5F5F7),
                onPrimary = ObsidianBg,
                background = ObsidianBg,
                surface = Color(0xFF111115),
                onBackground = TextPrimary,
                onSurface = TextPrimary
            )
            MaterialTheme(colorScheme = colorScheme) {
                AudioStudioProApp()
            }
        }
    }
}

@Composable
private fun AudioStudioProApp(vm: MusicViewModel = viewModel()) {
    val context = LocalContext.current
    val tracks by vm.tracks.collectAsState()
    val playlists by vm.playlists.collectAsState()
    val likedIds by vm.likedIds.collectAsState()
    val searchQuery by vm.searchQuery.collectAsState()
    val playback by vm.playbackState.collectAsState()
    val ambient by vm.ambientPalette.collectAsState()
    val dspState by vm.dspState.collectAsState()
    val isImporting by vm.isImporting.collectAsState()
    val banner by vm.statusBanner.collectAsState()

    var selectedTab by remember { mutableStateOf(LibraryTab.LIBRARY) }
    var openPlaylistId by remember { mutableStateOf<String?>(null) }
    var showNowPlaying by remember { mutableStateOf(false) }
    var showAudioLabSheet by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var trackForOptions by remember { mutableStateOf<AudioTrack?>(null) }
    var trackForPlaylistPicker by remember { mutableStateOf<AudioTrack?>(null) }

    // Intercept system back gesture so overlays collapse gracefully instead of exiting the app
    BackHandler(enabled = showNowPlaying) {
        showNowPlaying = false
    }
    BackHandler(enabled = !showNowPlaying && openPlaylistId != null) {
        openPlaylistId = null
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            vm.importAudioUris(uris)
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val animPrimary by animateColorAsState(
        targetValue = ambient.primary,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "ambientPrimary"
    )
    val animSecondary by animateColorAsState(
        targetValue = ambient.secondary,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "ambientSecondary"
    )
    val animAccent by animateColorAsState(
        targetValue = ambient.accent,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "ambientAccent"
    )

    val filteredTracks = remember(tracks, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) tracks
        else tracks.filter {
            it.title.lowercase().contains(q) ||
                it.artist.lowercase().contains(q) ||
                it.album.lowercase().contains(q)
        }
    }

    val likedTracks = remember(tracks, likedIds, searchQuery) {
        val base = tracks.filter { likedIds.contains(it.id) }
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) base
        else base.filter {
            it.title.lowercase().contains(q) || it.artist.lowercase().contains(q) || it.album.lowercase().contains(q)
        }
    }

    val activePlaylist = remember(playlists, openPlaylistId) {
        playlists.firstOrNull { it.id == openPlaylistId }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        // Dynamic Ambient Radial Lighting
        AmbientGlowCanvas(
            primary = animPrimary,
            secondary = animSecondary,
            intensity = if (playback.isPlaying) 0.34f else 0.18f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Studio Header
            StudioHeader(
                isImporting = isImporting,
                dspActive = dspState.hasActiveProcessing,
                accentColor = animAccent,
                onImportClick = { importLauncher.launch(arrayOf("audio/*")) },
                onAudioLabClick = { showAudioLabSheet = true }
            )

            // Segmented Apple-style Navigation Pill (hidden while inside a playlist detail)
            if (activePlaylist == null) {
                SegmentedGlassTabs(
                    selected = selectedTab,
                    onSelect = { selectedTab = it },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )

                if (selectedTab != LibraryTab.PLAYLISTS) {
                    GlassSearchBar(
                        query = searchQuery,
                        onQueryChange = vm::setSearchQuery,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }

            // Main Content Area
            Box(modifier = Modifier.weight(1f)) {
                when {
                    activePlaylist != null -> {
                        val trackMap = remember(tracks) { tracks.associateBy { it.id } }
                        val playlistTracks = remember(activePlaylist, trackMap) {
                            activePlaylist.trackIds.mapNotNull { trackMap[it] }
                        }
                        PlaylistDetailView(
                            playlist = activePlaylist,
                            tracks = playlistTracks,
                            currentTrackId = playback.currentTrack?.id,
                            isPlaying = playback.isPlaying,
                            accentColor = animAccent,
                            bottomPadding = if (playback.currentTrack != null) 112.dp else 28.dp,
                            onBack = { openPlaylistId = null },
                            onPlayAll = { vm.playTracks(playlistTracks, 0, shuffle = false) },
                            onShuffle = { vm.playTracks(playlistTracks, 0, shuffle = true) },
                            onTrackClick = { idx -> vm.playTracks(playlistTracks, idx, shuffle = false) },
                            onMoveUp = { idx -> vm.moveTrackInPlaylist(activePlaylist.id, idx, idx - 1) },
                            onMoveDown = { idx -> vm.moveTrackInPlaylist(activePlaylist.id, idx, idx + 1) },
                            onRemoveTrack = { trackId -> vm.removeTrackFromPlaylist(activePlaylist.id, trackId) },
                            onDeletePlaylist = {
                                vm.deletePlaylist(activePlaylist.id)
                                openPlaylistId = null
                            }
                        )
                    }

                    selectedTab == LibraryTab.LIBRARY -> {
                        TrackCollectionView(
                            tracks = filteredTracks,
                            likedIds = likedIds,
                            currentTrackId = playback.currentTrack?.id,
                            isPlaying = playback.isPlaying,
                            accentColor = animAccent,
                            emptyTitle = "Your Studio Library is Empty",
                            emptySubtitle = "Import lossless FLAC, WAV, MP3, or M4A tracks from your device. Files remain persisted across reboots.",
                            showImportCta = searchQuery.isBlank(),
                            bottomPadding = if (playback.currentTrack != null) 112.dp else 28.dp,
                            onImportClick = { importLauncher.launch(arrayOf("audio/*")) },
                            onPlayAll = { vm.playTracks(filteredTracks, 0, shuffle = false) },
                            onShuffle = { vm.playTracks(filteredTracks, 0, shuffle = true) },
                            onTrackClick = { idx -> vm.playTracks(filteredTracks, idx, shuffle = false) },
                            onToggleLike = vm::toggleLike,
                            onTrackOptions = { trackForOptions = it }
                        )
                    }

                    selectedTab == LibraryTab.LIKED -> {
                        TrackCollectionView(
                            tracks = likedTracks,
                            likedIds = likedIds,
                            currentTrackId = playback.currentTrack?.id,
                            isPlaying = playback.isPlaying,
                            accentColor = animAccent,
                            emptyTitle = "No Favorite Tracks Yet",
                            emptySubtitle = "Tap the heart on any track to curate your personal high-rotation collection.",
                            showImportCta = false,
                            bottomPadding = if (playback.currentTrack != null) 112.dp else 28.dp,
                            onImportClick = {},
                            onPlayAll = { vm.playTracks(likedTracks, 0, shuffle = false) },
                            onShuffle = { vm.playTracks(likedTracks, 0, shuffle = true) },
                            onTrackClick = { idx -> vm.playTracks(likedTracks, idx, shuffle = false) },
                            onToggleLike = vm::toggleLike,
                            onTrackOptions = { trackForOptions = it }
                        )
                    }

                    selectedTab == LibraryTab.PLAYLISTS -> {
                        PlaylistsGridList(
                            playlists = playlists,
                            allTracks = tracks,
                            accentColor = animAccent,
                            bottomPadding = if (playback.currentTrack != null) 112.dp else 28.dp,
                            onCreatePlaylist = { showCreatePlaylistDialog = true },
                            onOpenPlaylist = { openPlaylistId = it.id }
                        )
                    }
                }
            }
        }

        // Subtle Floating Status Pill
        AnimatedVisibility(
            visible = banner != null,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 64.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF1A1A20).copy(alpha = 0.94f),
                border = BorderStroke(1.dp, GlassBorder),
                shadowElevation = 12.dp
            ) {
                Text(
                    text = banner.orEmpty(),
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp)
                )
            }
        }

        // Floating Glass Mini-Player
        AnimatedVisibility(
            visible = playback.currentTrack != null && !showNowPlaying,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            playback.currentTrack?.let { current ->
                FloatingGlassMiniPlayer(
                    track = current,
                    isPlaying = playback.isPlaying,
                    progress = if (playback.durationMs > 0L) {
                        (playback.positionMs.toFloat() / playback.durationMs.toFloat()).coerceIn(0f, 1f)
                    } else 0f,
                    accentColor = animAccent,
                    onExpand = { showNowPlaying = true },
                    onTogglePlayPause = vm::togglePlayPause,
                    onSkipNext = vm::skipNext
                )
            }
        }

        // Full-Screen Now Playing Sheet with Spring Motion
        AnimatedVisibility(
            visible = showNowPlaying && playback.currentTrack != null,
            enter = slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) { it } + fadeIn(tween(220)),
            exit = slideOutVertically(
                animationSpec = tween(260, easing = FastOutSlowInEasing)
            ) { it } + fadeOut(tween(180))
        ) {
            playback.currentTrack?.let { current ->
                FullScreenNowPlaying(
                    track = current,
                    playback = playback,
                    dspState = dspState,
                    isLiked = likedIds.contains(current.id),
                    primaryColor = animPrimary,
                    secondaryColor = animSecondary,
                    accentColor = animAccent,
                    onCollapse = { showNowPlaying = false },
                    onTogglePlayPause = vm::togglePlayPause,
                    onSkipPrev = vm::skipPrevious,
                    onSkipNext = vm::skipNext,
                    onSeek = vm::seekTo,
                    onToggleShuffle = vm::toggleShuffle,
                    onCycleRepeat = vm::cycleRepeatMode,
                    onToggleLike = { vm.toggleLike(current.id) },
                    onOpenAudioLab = { showAudioLabSheet = true },
                    onOpenQueue = { showQueueSheet = true }
                )
            }
        }
    }

    // Pro Audio Lab Bottom Sheet (EQ, Bass Boost, Varispeed, Pitch, A-B Looper, Sleep Timer, Soft Auto-Fade)
    if (showAudioLabSheet) {
        AudioLabBottomSheet(
            dspState = dspState,
            playback = playback,
            accentColor = animAccent,
            onDismiss = { showAudioLabSheet = false },
            onToggleEq = vm::setEqEnabled,
            onSelectPreset = vm::applyEqPreset,
            onBandChange = vm::setEqBandGain,
            onBassBoostChange = vm::setBassBoost,
            onSpeedChange = vm::setSpeed,
            onPitchChange = vm::setPitch,
            onResetSpeedPitch = vm::resetTempoPitch,
            onMarkA = vm::markLoopA,
            onMarkB = vm::markLoopB,
            onToggleAbLoop = vm::toggleAbLoop,
            onClearAbLoop = vm::clearAbLoop,
            onSetSleepMinutes = vm::setSleepTimerMinutes,
            onCrossfadeChange = vm::setCrossfadeSeconds
        )
    }

    // Active Queue Bottom Sheet
    if (showQueueSheet) {
        QueueBottomSheet(
            queue = playback.queue,
            currentIndex = playback.currentQueueIndex,
            accentColor = animAccent,
            onDismiss = { showQueueSheet = false },
            onSelectIndex = vm::jumpToQueueIndex,
            onRemoveIndex = vm::removeFromQueue
        )
    }

    // Track Context Action Sheet
    trackForOptions?.let { track ->
        TrackOptionsSheet(
            track = track,
            isLiked = likedIds.contains(track.id),
            onDismiss = { trackForOptions = null },
            onPlayNext = {
                vm.playNext(track)
                trackForOptions = null
            },
            onAddToPlaylist = {
                trackForPlaylistPicker = track
                trackForOptions = null
            },
            onToggleLike = {
                vm.toggleLike(track.id)
                trackForOptions = null
            },
            onRemoveFromLibrary = {
                vm.removeTrackFromLibrary(track)
                trackForOptions = null
            }
        )
    }

    // Playlist Picker Sheet
    trackForPlaylistPicker?.let { track ->
        PlaylistPickerSheet(
            track = track,
            playlists = playlists,
            onDismiss = { trackForPlaylistPicker = null },
            onPickPlaylist = { playlistId ->
                vm.addTrackToPlaylist(playlistId, track.id)
                trackForPlaylistPicker = null
            },
            onCreateNew = {
                trackForPlaylistPicker = null
                showCreatePlaylistDialog = true
            }
        )
    }

    // Create Playlist Dialog
    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onConfirm = { name ->
                vm.createPlaylist(name)
                showCreatePlaylistDialog = false
            }
        )
    }
}

@Composable
private fun AmbientGlowCanvas(
    primary: Color,
    secondary: Color,
    intensity: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primary.copy(alpha = intensity), Color.Transparent),
                center = Offset(size.width * 0.18f, size.height * 0.12f),
                radius = size.width * 0.95f
            ),
            radius = size.width * 0.95f,
            center = Offset(size.width * 0.18f, size.height * 0.12f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(secondary.copy(alpha = intensity * 0.9f), Color.Transparent),
                center = Offset(size.width * 0.85f, size.height * 0.72f),
                radius = size.width * 0.9f
            ),
            radius = size.width * 0.9f,
            center = Offset(size.width * 0.85f, size.height * 0.72f)
        )
    }
}

@Composable
private fun StudioHeader(
    isImporting: Boolean,
    dspActive: Boolean,
    accentColor: Color,
    onImportClick: () -> Unit,
    onAudioLabClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "AudioStudio",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.6).sp
            )
            Text(
                text = "PRO LOCAL ENGINE",
                color = TextTertiary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.6.sp
            )
        }

        // DSP Lab Quick Pill
        Surface(
            onClick = onAudioLabClick,
            shape = CircleShape,
            color = if (dspActive) accentColor.copy(alpha = 0.22f) else GlassSurfaceLow,
            border = BorderStroke(
                1.dp,
                if (dspActive) accentColor.copy(alpha = 0.55f) else GlassBorder
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = "Audio Lab",
                    tint = if (dspActive) accentColor else TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "DSP",
                    color = if (dspActive) accentColor else TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Prominent + Import Pill
        Surface(
            onClick = onImportClick,
            enabled = !isImporting,
            shape = CircleShape,
            color = TextPrimary,
            contentColor = ObsidianBg
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (isImporting) {
                    CircularProgressIndicator(
                        color = ObsidianBg,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(15.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Import Audio",
                        tint = ObsidianBg,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = if (isImporting) "Importing" else "Import",
                    color = ObsidianBg,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SegmentedGlassTabs(
    selected: LibraryTab,
    onSelect: (LibraryTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(GlassSurfaceLow)
            .border(1.dp, GlassBorder, CircleShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LibraryTab.entries.forEach { tab ->
            val active = tab == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(if (active) Color.White.copy(alpha = 0.14f) else Color.Transparent)
                    .clickable { onSelect(tab) }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.label,
                    color = if (active) TextPrimary else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun GlassSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(GlassSurfaceLow)
            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = "Search songs, artists, or albums",
                    color = TextTertiary,
                    fontSize = 14.sp
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(color = TextPrimary, fontSize = 14.sp),
                cursorBrush = SolidColor(TextPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (query.isNotEmpty()) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Clear search",
                tint = TextSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onQueryChange("") }
            )
        }
    }
}

@Composable
private fun TrackCollectionView(
    tracks: List<AudioTrack>,
    likedIds: Set<String>,
    currentTrackId: String?,
    isPlaying: Boolean,
    accentColor: Color,
    emptyTitle: String,
    emptySubtitle: String,
    showImportCta: Boolean,
    bottomPadding: Dp,
    onImportClick: () -> Unit,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onTrackClick: (Int) -> Unit,
    onToggleLike: (String) -> Unit,
    onTrackOptions: (AudioTrack) -> Unit
) {
    if (tracks.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(GlassSurfaceMid)
                    .border(1.dp, GlassBorder, RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = emptyTitle,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = emptySubtitle,
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            if (showImportCta) {
                Spacer(modifier = Modifier.height(22.dp))
                Surface(
                    onClick = onImportClick,
                    shape = CircleShape,
                    color = TextPrimary
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, tint = ObsidianBg)
                        Text(
                            text = "Import Local Music",
                            color = ObsidianBg,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 6.dp,
            bottom = bottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassActionPill(
                    label = "Play All",
                    icon = Icons.Rounded.PlayArrow,
                    emphasized = true,
                    accentColor = accentColor,
                    onClick = onPlayAll,
                    modifier = Modifier.weight(1f)
                )
                GlassActionPill(
                    label = "Shuffle",
                    icon = Icons.Rounded.Shuffle,
                    emphasized = false,
                    accentColor = accentColor,
                    onClick = onShuffle,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        itemsIndexed(tracks, key = { _, t -> t.id }) { index, track ->
            val isCurrent = track.id == currentTrackId
            TrackRowCard(
                track = track,
                isCurrent = isCurrent,
                isPlaying = isCurrent && isPlaying,
                isLiked = likedIds.contains(track.id),
                accentColor = accentColor,
                onClick = { onTrackClick(index) },
                onToggleLike = { onToggleLike(track.id) },
                onMoreClick = { onTrackOptions(track) }
            )
        }
    }
}

@Composable
private fun GlassActionPill(
    label: String,
    icon: ImageVector,
    emphasized: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (emphasized) accentColor.copy(alpha = 0.20f) else GlassSurfaceLow,
        border = BorderStroke(
            1.dp,
            if (emphasized) accentColor.copy(alpha = 0.45f) else GlassBorder
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (emphasized) accentColor else TextPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TrackRowCard(
    track: AudioTrack,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isLiked: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    onToggleLike: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isCurrent) accentColor.copy(alpha = 0.12f) else GlassSurfaceLow)
            .border(
                width = 1.dp,
                color = if (isCurrent) accentColor.copy(alpha = 0.40f) else GlassBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            TrackArtwork(
                artworkPath = track.artworkPath,
                seed = track.id,
                size = 50.dp,
                cornerRadius = 11.dp
            )
            if (isCurrent) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    AcousticPulseBars(
                        isPlaying = isPlaying,
                        barColor = accentColor,
                        barCount = 3,
                        maxHeight = 18.dp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = if (isCurrent) accentColor else TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${track.artist} • ${formatDuration(track.durationMs)}",
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onToggleLike, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isLiked) Color(0xFFFF375F) else TextTertiary,
                modifier = Modifier.size(19.dp)
            )
        }

        IconButton(onClick = onMoreClick, modifier = Modifier.size(34.dp)) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "Track options",
                tint = TextSecondary,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

@Composable
private fun PlaylistsGridList(
    playlists: List<UserPlaylist>,
    allTracks: List<AudioTrack>,
    accentColor: Color,
    bottomPadding: Dp,
    onCreatePlaylist: () -> Unit,
    onOpenPlaylist: (UserPlaylist) -> Unit
) {
    val trackMap = remember(allTracks) { allTracks.associateBy { it.id } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = bottomPadding),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                onClick = onCreatePlaylist,
                shape = RoundedCornerShape(16.dp),
                color = accentColor.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, tint = accentColor)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "New Custom Playlist",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (playlists.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Create custom mixes and reorder tracks effortlessly.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(playlists, key = { it.id }) { playlist ->
                val coverTrack = playlist.trackIds.firstOrNull()?.let { trackMap[it] }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassSurfaceLow)
                        .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                        .clickable { onOpenPlaylist(playlist) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TrackArtwork(
                        artworkPath = coverTrack?.artworkPath,
                        seed = playlist.id,
                        size = 54.dp,
                        cornerRadius = 12.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = playlist.name,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${playlist.trackIds.size} track${if (playlist.trackIds.size == 1) "" else "s"}",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                        contentDescription = null,
                        tint = TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistDetailView(
    playlist: UserPlaylist,
    tracks: List<AudioTrack>,
    currentTrackId: String?,
    isPlaying: Boolean,
    accentColor: Color,
    bottomPadding: Dp,
    onBack: () -> Unit,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onTrackClick: (Int) -> Unit,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onRemoveTrack: (String) -> Unit,
    onDeletePlaylist: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.name,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${tracks.size} tracks • Use arrows to reorder",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            IconButton(onClick = onDeletePlaylist) {
                Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete playlist", tint = Color(0xFFFF6B6B))
            }
        }

        if (tracks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add tracks from your Library using the ⋮ menu.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = bottomPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlassActionPill("Play", Icons.Rounded.PlayArrow, true, accentColor, onPlayAll, Modifier.weight(1f))
                        GlassActionPill("Shuffle", Icons.Rounded.Shuffle, false, accentColor, onShuffle, Modifier.weight(1f))
                    }
                }
                itemsIndexed(tracks, key = { idx, t -> "${t.id}_$idx" }) { index, track ->
                    val isCurrent = track.id == currentTrackId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isCurrent) accentColor.copy(alpha = 0.12f) else GlassSurfaceLow)
                            .border(1.dp, if (isCurrent) accentColor.copy(alpha = 0.4f) else GlassBorder, RoundedCornerShape(16.dp))
                            .clickable { onTrackClick(index) }
                            .padding(horizontal = 10.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            TrackArtwork(track.artworkPath, track.id, 46.dp, 10.dp)
                            if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black.copy(alpha = 0.45f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AcousticPulseBars(
                                        isPlaying = isPlaying,
                                        barColor = accentColor,
                                        barCount = 3,
                                        maxHeight = 16.dp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                color = if (isCurrent) accentColor else TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = track.artist,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (index > 0) {
                            IconButton(onClick = { onMoveUp(index) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Rounded.ArrowUpward, contentDescription = "Move up", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                        if (index < tracks.lastIndex) {
                            IconButton(onClick = { onMoveDown(index) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Rounded.ArrowDownward, contentDescription = "Move down", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                        IconButton(onClick = { onRemoveTrack(track.id) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Rounded.Close, contentDescription = "Remove", tint = TextTertiary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingGlassMiniPlayer(
    track: AudioTrack,
    isPlaying: Boolean,
    progress: Float,
    accentColor: Color,
    onExpand: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit
) {
    Surface(
        onClick = onExpand,
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF14141A).copy(alpha = 0.88f),
        border = BorderStroke(1.dp, GlassBorder),
        shadowElevation = 18.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TrackArtwork(
                    artworkPath = track.artworkPath,
                    seed = track.id,
                    size = 44.dp,
                    cornerRadius = 10.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onTogglePlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play or Pause",
                        tint = TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                IconButton(onClick = onSkipNext) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Next Track",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(2.5.dp)
                        .background(accentColor)
                )
            }
        }
    }
}

@Composable
private fun FullScreenNowPlaying(
    track: AudioTrack,
    playback: PlaybackUiState,
    dspState: DspState,
    isLiked: Boolean,
    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color,
    onCollapse: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipPrev: () -> Unit,
    onSkipNext: () -> Unit,
    onSeek: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleLike: () -> Unit,
    onOpenAudioLab: () -> Unit,
    onOpenQueue: () -> Unit
) {
    // Tactile Apple Music spring-physics scale: 0.86f paused -> 1.0f playing
    val artworkScale by animateFloatAsState(
        targetValue = if (playback.isPlaying) 1.0f else 0.86f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "artworkSpringScale"
    )

    var isScrubbing by remember { mutableStateOf(false) }
    var scrubValue by remember { mutableFloatStateOf(0f) }

    val duration = playback.durationMs.coerceAtLeast(1L)
    val displayedPositionMs = if (isScrubbing) {
        (scrubValue * duration).toLong()
    } else {
        playback.positionMs.coerceIn(0L, duration)
    }
    val sliderFraction = if (isScrubbing) {
        scrubValue
    } else {
        (playback.positionMs.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        AmbientGlowCanvas(
            primary = primaryColor,
            secondary = secondaryColor,
            intensity = if (playback.isPlaying) 0.50f else 0.28f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCollapse) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = TextPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "NOW PLAYING",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.6.sp
                    )
                    Text(
                        text = track.album,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                AcousticPulseBars(
                    isPlaying = playback.isPlaying,
                    barColor = accentColor,
                    barCount = 4,
                    maxHeight = 18.dp,
                    speedMultiplier = dspState.speed
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Hero Artwork with Spring Physics
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .aspectRatio(1f)
                    .graphicsLayer {
                        scaleX = artworkScale
                        scaleY = artworkScale
                    }
                    .shadow(
                        elevation = if (playback.isPlaying) 32.dp else 10.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = primaryColor,
                        spotColor = primaryColor
                    )
            ) {
                TrackArtwork(
                    artworkPath = track.artworkPath,
                    seed = track.id,
                    size = null,
                    cornerRadius = 24.dp,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.weight(0.6f))

            // Track Metadata & Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = track.artist,
                        color = TextSecondary,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onToggleLike) {
                    Icon(
                        imageVector = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "Like track",
                        tint = if (isLiked) Color(0xFFFF375F) else TextSecondary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scrubber
            Slider(
                value = sliderFraction,
                onValueChange = {
                    isScrubbing = true
                    scrubValue = it
                },
                onValueChangeFinished = {
                    onSeek((scrubValue * duration).toLong())
                    isScrubbing = false
                },
                colors = SliderDefaults.colors(
                    thumbColor = TextPrimary,
                    activeTrackColor = TextPrimary,
                    inactiveTrackColor = Color.White.copy(alpha = 0.18f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(displayedPositionMs),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                if (dspState.abLoop.enabled && dspState.abLoop.isReady) {
                    Text(
                        text = "A-B ${formatDuration(dspState.abLoop.pointAMs ?: 0L)}–${formatDuration(dspState.abLoop.pointBMs ?: 0L)}",
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = formatDuration(playback.durationMs),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Transport Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleShuffle) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playback.shuffleEnabled) accentColor else TextTertiary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(onClick = onSkipPrev, modifier = Modifier.size(54.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = "Previous",
                        tint = TextPrimary,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Surface(
                    onClick = onTogglePlayPause,
                    shape = CircleShape,
                    color = TextPrimary,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (playback.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = "Play or Pause",
                            tint = ObsidianBg,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                IconButton(onClick = onSkipNext, modifier = Modifier.size(54.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Next",
                        tint = TextPrimary,
                        modifier = Modifier.size(38.dp)
                    )
                }

                IconButton(onClick = onCycleRepeat) {
                    val (icon, tint) = when (playback.repeatMode) {
                        Player.REPEAT_MODE_ONE -> Icons.Rounded.RepeatOne to accentColor
                        Player.REPEAT_MODE_ALL -> Icons.Rounded.Repeat to accentColor
                        else -> Icons.Rounded.Repeat to TextTertiary
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Repeat",
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Bottom Glass Utility Dock (Progressive Disclosure for DSP & Queue)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    onClick = onOpenAudioLab,
                    shape = CircleShape,
                    color = if (dspState.hasActiveProcessing) accentColor.copy(alpha = 0.22f) else GlassSurfaceMid,
                    border = BorderStroke(
                        1.dp,
                        if (dspState.hasActiveProcessing) accentColor.copy(alpha = 0.55f) else GlassBorder
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GraphicEq,
                            contentDescription = null,
                            tint = if (dspState.hasActiveProcessing) accentColor else TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (dspState.sleepTimerRemainingMs > 0L) {
                                "Lab • ${formatDuration(dspState.sleepTimerRemainingMs)}"
                            } else {
                                "Audio Lab"
                            },
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    onClick = onOpenQueue,
                    shape = CircleShape,
                    color = GlassSurfaceMid,
                    border = BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Up Next (${playback.queue.size})",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioLabBottomSheet(
    dspState: DspState,
    playback: PlaybackUiState,
    accentColor: Color,
    onDismiss: () -> Unit,
    onToggleEq: (Boolean) -> Unit,
    onSelectPreset: (EqPreset) -> Unit,
    onBandChange: (Int, Float) -> Unit,
    onBassBoostChange: (Int) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onPitchChange: (Float) -> Unit,
    onResetSpeedPitch: () -> Unit,
    onMarkA: () -> Unit,
    onMarkB: () -> Unit,
    onToggleAbLoop: (Boolean) -> Unit,
    onClearAbLoop: () -> Unit,
    onSetSleepMinutes: (Int) -> Unit,
    onCrossfadeChange: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var activeTab by remember { mutableStateOf(AudioLabTab.EQ) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF111116),
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = "Pro DSP Audio Lab",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Hardware-bound session processing — persists in background",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Segmented Audio Lab Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(GlassSurfaceLow)
                    .border(1.dp, GlassBorder, CircleShape)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AudioLabTab.entries.forEach { tab ->
                    val selected = tab == activeTab
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .background(if (selected) accentColor.copy(alpha = 0.24f) else Color.Transparent)
                            .clickable { activeTab = tab }
                            .padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = if (selected) accentColor else TextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = tab.label,
                            color = if (selected) TextPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            when (activeTab) {
                AudioLabTab.EQ -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("5-Band Hardware Equalizer", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text("Range -12dB to +12dB", color = TextSecondary, fontSize = 12.sp)
                        }
                        Switch(
                            checked = dspState.eqEnabled,
                            onCheckedChange = onToggleEq,
                            colors = SwitchDefaults.colors(checkedTrackColor = accentColor)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val presets = listOf(
                            EqPreset.FLAT,
                            EqPreset.BASS_BOOST,
                            EqPreset.VOCAL_CLARITY,
                            EqPreset.LATE_NIGHT,
                            EqPreset.STUDIO_AIR
                        )
                        items(presets) { preset ->
                            val selected = dspState.eqEnabled && dspState.activePreset == preset
                            Surface(
                                onClick = { onSelectPreset(preset) },
                                shape = CircleShape,
                                color = if (selected) accentColor.copy(alpha = 0.25f) else GlassSurfaceLow,
                                border = BorderStroke(1.dp, if (selected) accentColor else GlassBorder)
                            ) {
                                Text(
                                    text = preset.label,
                                    color = if (selected) TextPrimary else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    StudioEngineBridge.bandLabels.forEachIndexed { idx, freq ->
                        val gain = dspState.bandGainsDb.getOrElse(idx) { 0f }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = freq,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.width(48.dp)
                            )
                            Slider(
                                value = gain,
                                onValueChange = { onBandChange(idx, it) },
                                valueRange = -12f..12f,
                                colors = SliderDefaults.colors(
                                    thumbColor = if (dspState.eqEnabled) accentColor else TextTertiary,
                                    activeTrackColor = if (dspState.eqEnabled) accentColor else TextTertiary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = String.format(Locale.US, "%+.1fdB", gain),
                                color = TextPrimary,
                                fontSize = 12.sp,
                                modifier = Modifier.width(56.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Bass", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(48.dp))
                        Slider(
                            value = dspState.bassBoostStrength.toFloat(),
                            onValueChange = { onBassBoostChange(it.roundToInt()) },
                            valueRange = 0f..1000f,
                            colors = SliderDefaults.colors(
                                thumbColor = accentColor,
                                activeTrackColor = accentColor
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${dspState.bassBoostStrength / 10}%",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            modifier = Modifier.width(56.dp)
                        )
                    }
                }

                AudioLabTab.SPEED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Varispeed Tempo & Pitch", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        TextButton(onClick = onResetSpeedPitch) {
                            Text("Reset 1.00x", color = accentColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = String.format(Locale.US, "Playback Tempo: %.2fx", dspState.speed),
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Slider(
                        value = dspState.speed,
                        onValueChange = onSpeedChange,
                        valueRange = 0.50f..2.00f,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { presetSpeed ->
                            Surface(
                                onClick = { onSpeedChange(presetSpeed) },
                                shape = CircleShape,
                                color = GlassSurfaceLow,
                                border = BorderStroke(1.dp, GlassBorder)
                            ) {
                                Text(
                                    text = "${presetSpeed}x",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = String.format(Locale.US, "Independent Pitch: %.2fx", dspState.pitch),
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Slider(
                        value = dspState.pitch,
                        onValueChange = onPitchChange,
                        valueRange = 0.50f..2.00f,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )
                }

                AudioLabTab.FLOW -> {
                    // A-B Section Looper
                    Text("A-B Section Looper", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (playback.currentTrack == null) {
                            "Start playing a track to mark loop points."
                        } else {
                            "A: ${dspState.abLoop.pointAMs?.let { formatDuration(it) } ?: "--:--"}   •   B: ${dspState.abLoop.pointBMs?.let { formatDuration(it) } ?: "--:--"}"
                        },
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            onClick = onMarkA,
                            enabled = playback.currentTrack != null,
                            shape = RoundedCornerShape(12.dp),
                            color = GlassSurfaceMid,
                            border = BorderStroke(1.dp, GlassBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text("Set A", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Surface(
                            onClick = onMarkB,
                            enabled = playback.currentTrack != null,
                            shape = RoundedCornerShape(12.dp),
                            color = GlassSurfaceMid,
                            border = BorderStroke(1.dp, GlassBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text("Set B", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Surface(
                            onClick = { onToggleAbLoop(!dspState.abLoop.enabled) },
                            enabled = dspState.abLoop.isReady,
                            shape = RoundedCornerShape(12.dp),
                            color = if (dspState.abLoop.enabled) accentColor.copy(alpha = 0.25f) else GlassSurfaceLow,
                            border = BorderStroke(1.dp, if (dspState.abLoop.enabled) accentColor else GlassBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    if (dspState.abLoop.enabled) "Active" else "Loop",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Surface(
                            onClick = onClearAbLoop,
                            shape = RoundedCornerShape(12.dp),
                            color = GlassSurfaceLow,
                            border = BorderStroke(1.dp, GlassBorder)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                            ) {
                                Text("Clear", color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = GlassBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Sleep Timer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Bedtime, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Fade-Out Sleep Timer", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                        if (dspState.sleepTimerRemainingMs > 0L) {
                            Text(
                                text = formatDuration(dspState.sleepTimerRemainingMs),
                                color = accentColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0 to "Off", 15 to "15m", 30 to "30m", 45 to "45m", 60 to "60m").forEach { (mins, label) ->
                            val isSelected = dspState.sleepTimerSelectedMinutes == mins
                            Surface(
                                onClick = { onSetSleepMinutes(mins) },
                                shape = CircleShape,
                                color = if (isSelected) accentColor.copy(alpha = 0.25f) else GlassSurfaceLow,
                                border = BorderStroke(1.dp, if (isSelected) accentColor else GlassBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 9.dp)
                                ) {
                                    Text(label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = GlassBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Soft Auto-Fade (Defaults to True Gapless = 0s)
                    val fadeLabel = if (dspState.crossfadeSeconds == 0) {
                        "True Gapless (Off)"
                    } else {
                        "${dspState.crossfadeSeconds}s Soft Auto-Fade"
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Track Transitions", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text(fadeLabel, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Slider(
                        value = dspState.crossfadeSeconds.toFloat(),
                        onValueChange = { onCrossfadeChange(it.roundToInt()) },
                        valueRange = 0f..8f,
                        steps = 7,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun QueueBottomSheet(
    queue: List<AudioTrack>,
    currentIndex: Int,
    accentColor: Color,
    onDismiss: () -> Unit,
    onSelectIndex: (Int) -> Unit,
    onRemoveIndex: (Int) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF111116),
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = "Playing Queue (${queue.size})",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (queue.isEmpty()) {
                Text("Queue is empty.", color = TextSecondary, modifier = Modifier.padding(vertical = 24.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(queue, key = { idx, t -> "${t.id}_q_$idx" }) { index, track ->
                        val active = index == currentIndex
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (active) accentColor.copy(alpha = 0.16f) else GlassSurfaceLow)
                                .border(1.dp, if (active) accentColor.copy(alpha = 0.4f) else GlassBorder, RoundedCornerShape(14.dp))
                                .clickable { onSelectIndex(index) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TrackArtwork(track.artworkPath, track.id, 42.dp, 9.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    color = if (active) accentColor else TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = track.artist,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (!active) {
                                IconButton(onClick = { onRemoveIndex(index) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Remove", tint = TextTertiary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackOptionsSheet(
    track: AudioTrack,
    isLiked: Boolean,
    onDismiss: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onToggleLike: () -> Unit,
    onRemoveFromLibrary: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF111116),
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(track.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist, color = TextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(10.dp))

            SheetActionItem("Play Next", Icons.Rounded.QueuePlayNext, TextPrimary, onPlayNext)
            SheetActionItem("Add to Playlist", Icons.AutoMirrored.Rounded.PlaylistAdd, TextPrimary, onAddToPlaylist)
            SheetActionItem(
                if (isLiked) "Remove from Favorites" else "Add to Favorites",
                if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                if (isLiked) Color(0xFFFF375F) else TextPrimary,
                onToggleLike
            )
            SheetActionItem("Remove from Library", Icons.Rounded.DeleteOutline, Color(0xFFFF6B6B), onRemoveFromLibrary)
        }
    }
}

@Composable
private fun SheetActionItem(
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(21.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(label, color = tint, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PlaylistPickerSheet(
    track: AudioTrack,
    playlists: List<UserPlaylist>,
    onDismiss: () -> Unit,
    onPickPlaylist: (String) -> Unit,
    onCreateNew: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF111116),
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text("Add \"${track.title}\" to...", fontWeight = FontWeight.Bold, fontSize = 17.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(12.dp))
            SheetActionItem("Create New Playlist", Icons.Rounded.Add, Color(0xFF818CF8), onCreateNew)
            playlists.forEach { playlist ->
                SheetActionItem(
                    label = "${playlist.name} (${playlist.trackIds.size})",
                    icon = Icons.AutoMirrored.Rounded.QueueMusic,
                    tint = TextPrimary,
                    onClick = { onPickPlaylist(playlist.id) }
                )
            }
        }
    }
}

@Composable
private fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF18181F),
        title = { Text("New Playlist", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                placeholder = { Text("Late Night Vinyl", color = TextTertiary) }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name) }
            ) {
                Text("Create", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun TrackArtwork(
    artworkPath: String?,
    seed: String,
    size: Dp?,
    cornerRadius: Dp,
    modifier: Modifier = Modifier
) {
    val imageBitmap by produceState<ImageBitmap?>(initialValue = null, key1 = artworkPath) {
        value = ArtworkCache.loadBitmap(artworkPath)?.asImageBitmap()
    }

    val fallbackPalette = remember(seed) { AmbientPalette.fallbackFor(seed) }
    val sizedModifier = if (size != null) modifier.size(size) else modifier

    Box(
        modifier = sizedModifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    listOf(fallbackPalette.primary, fallbackPalette.secondary)
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap!!,
                contentDescription = "Album art",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(if (size != null) size * 0.44f else 72.dp)
            )
        }
    }
}

/**
 * Privacy-respecting, zero-permission acoustic pulse visualizer.
 * Adapts cadence to the active varispeed tempo and rests smoothly when paused.
 */
@Composable
private fun AcousticPulseBars(
    isPlaying: Boolean,
    barColor: Color,
    barCount: Int,
    maxHeight: Dp,
    speedMultiplier: Float = 1.0f
) {
    val infinite = rememberInfiniteTransition(label = "acousticPulse")
    val durationScale = (1f / speedMultiplier.coerceIn(0.5f, 2.0f))

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(maxHeight)
    ) {
        for (i in 0 until barCount) {
            val animFraction by infinite.animateFloat(
                initialValue = 0.25f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = ((360 + i * 110) * durationScale).roundToInt().coerceAtLeast(180),
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$i"
            )
            val heightFraction = if (isPlaying) animFraction else 0.25f
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(maxHeight * heightFraction)
                    .clip(CircleShape)
                    .background(barColor)
            )
        }
    }
}
