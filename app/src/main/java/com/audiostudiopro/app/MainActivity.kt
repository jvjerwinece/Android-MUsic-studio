@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

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
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.QueuePlayNext
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.Player
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

// Spotify Encore + Apple HIG Matte Obsidian Design Tokens
private val CanvasBg = Color(0xFF0D0D10)
private val SurfaceElevated = Color(0xFF17171C)
private val SurfaceChip = Color(0xFF222229)
private val SurfaceSheet = Color(0xFF141419)
private val DividerSubtle = Color.White.copy(alpha = 0.08f)
private val GlassDock = Color.White.copy(alpha = 0.09f)
private val GlassBorder = Color.White.copy(alpha = 0.14f)
private val TextPrimary = Color(0xFFF5F5F7)
private val TextSecondary = Color(0xFFA0A0AA)
private val TextMuted = Color(0xFF6B6B76)
private val SpotifyAccentFallback = Color(0xFF1ED760)

private enum class MainTab(val label: String, val icon: ImageVector) {
    LIBRARY("Library", Icons.Rounded.LibraryMusic),
    SEARCH("Search", Icons.Rounded.Search),
    PLAYLISTS("Playlists", Icons.AutoMirrored.Rounded.QueueMusic),
    BEAM("Beam", Icons.Rounded.WifiTethering)
}

private enum class AudioLabTab(val label: String) {
    EQ("EQ"),
    STAGE("Stage"),
    SPEED("Speed"),
    FLOW("Flow")
}

private enum class BeamModeTab(val label: String) {
    SEND("Send"),
    RECEIVE("Receive")
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
                primary = TextPrimary,
                onPrimary = CanvasBg,
                background = CanvasBg,
                surface = SurfaceElevated,
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
    val customEqPresets by vm.customEqPresets.collectAsState()
    val likedIds by vm.likedIds.collectAsState()
    val searchQuery by vm.searchQuery.collectAsState()
    val sortOrder by vm.sortOrder.collectAsState()
    val filterMode by vm.filterMode.collectAsState()
    val playback by vm.playbackState.collectAsState()
    val ambient by vm.ambientPalette.collectAsState()
    val dspState by vm.dspState.collectAsState()
    val beamState by vm.beamState.collectAsState()
    val isImporting by vm.isImporting.collectAsState()
    val banner by vm.statusBanner.collectAsState()

    var currentTab by remember { mutableStateOf(MainTab.LIBRARY) }
    var openPlaylistId by remember { mutableStateOf<String?>(null) }
    var viewingLikedCollection by remember { mutableStateOf(false) }

    var showNowPlaying by remember { mutableStateOf(false) }
    var showAudioLabSheet by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    var beamTab by remember { mutableStateOf(BeamModeTab.SEND) }
    var beamPreselectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    var trackForOptions by remember { mutableStateOf<AudioTrack?>(null) }
    var trackForMetadataEdit by remember { mutableStateOf<AudioTrack?>(null) }
    var trackForPlaylistPicker by remember { mutableStateOf<AudioTrack?>(null) }

    // Long-press multi-select batch mode
    var multiSelectIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    val isMultiSelectActive = multiSelectIds.isNotEmpty()

    BackHandler(enabled = showNowPlaying) { showNowPlaying = false }
    BackHandler(enabled = !showNowPlaying && isMultiSelectActive) { multiSelectIds = emptySet() }
    BackHandler(enabled = !showNowPlaying && !isMultiSelectActive && (openPlaylistId != null || viewingLikedCollection)) {
        openPlaylistId = null
        viewingLikedCollection = false
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) vm.importAudioUris(uris)
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
        animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing),
        label = "ambientPrimary"
    )
    val animSecondary by animateColorAsState(
        targetValue = ambient.secondary,
        animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing),
        label = "ambientSecondary"
    )
    val animAccent by animateColorAsState(
        targetValue = if (playback.currentTrack != null) ambient.accent else SpotifyAccentFallback,
        animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing),
        label = "ambientAccent"
    )

    val libraryTracks = remember(tracks, likedIds, sortOrder, filterMode) {
        val filtered = when (filterMode) {
            LibraryFilter.ALL -> tracks
            LibraryFilter.FAVORITES -> tracks.filter { likedIds.contains(it.id) }
            LibraryFilter.LOSSLESS -> tracks.filter { it.isLossless }
            LibraryFilter.RECENTLY_PLAYED -> tracks.filter { it.lastPlayedAt > 0L }
        }
        when (sortOrder) {
            LibrarySort.RECENT -> filtered.sortedByDescending { it.dateAdded }
            LibrarySort.TITLE -> filtered.sortedBy { it.title.lowercase(Locale.US) }
            LibrarySort.ARTIST -> filtered.sortedBy { it.artist.lowercase(Locale.US) }
            LibrarySort.DURATION -> filtered.sortedByDescending { it.durationMs }
            LibrarySort.MOST_PLAYED -> filtered.sortedByDescending { it.playCount }
        }
    }

    val searchResults = remember(tracks, searchQuery) {
        val q = searchQuery.trim().lowercase(Locale.US)
        if (q.isEmpty()) emptyList()
        else tracks.filter {
            it.title.lowercase(Locale.US).contains(q) ||
                it.artist.lowercase(Locale.US).contains(q) ||
                it.album.lowercase(Locale.US).contains(q) ||
                it.codec.lowercase(Locale.US).contains(q)
        }
    }

    val likedTracks = remember(tracks, likedIds) {
        tracks.filter { likedIds.contains(it.id) }
    }

    val activePlaylist = remember(playlists, openPlaylistId) {
        playlists.firstOrNull { it.id == openPlaylistId }
    }

    fun openBeamWithTracks(ids: Set<String>, mode: BeamModeTab = BeamModeTab.SEND) {
        beamPreselectedIds = ids
        beamTab = mode
        showNowPlaying = false
        currentTab = MainTab.BEAM
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Main Screen Content
            Box(modifier = Modifier.weight(1f)) {
                val listBottomPad = when {
                    isMultiSelectActive -> 84.dp
                    playback.currentTrack != null -> 78.dp
                    else -> 16.dp
                }

                when {
                    // Playlist Detail Overlay inside Playlists Tab
                    currentTab == MainTab.PLAYLISTS && activePlaylist != null -> {
                        val trackMap = remember(tracks) { tracks.associateBy { it.id } }
                        val playlistTracks = remember(activePlaylist, trackMap) {
                            activePlaylist.trackIds.mapNotNull { trackMap[it] }
                        }
                        PlaylistDetailScreen(
                            playlist = activePlaylist,
                            tracks = playlistTracks,
                            currentTrackId = playback.currentTrack?.id,
                            isPlaying = playback.isPlaying,
                            accentColor = animAccent,
                            bottomPadding = listBottomPad,
                            onBack = { openPlaylistId = null },
                            onPlayAll = { vm.playTracks(playlistTracks, 0, shuffle = false) },
                            onShuffle = { vm.playTracks(playlistTracks, 0, shuffle = true) },
                            onBeamPlaylist = { openBeamWithTracks(playlistTracks.map { it.id }.toSet()) },
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

                    // Liked Songs Hero Detail inside Playlists Tab
                    currentTab == MainTab.PLAYLISTS && viewingLikedCollection -> {
                        LikedCollectionScreen(
                            tracks = likedTracks,
                            currentTrackId = playback.currentTrack?.id,
                            isPlaying = playback.isPlaying,
                            accentColor = animAccent,
                            bottomPadding = listBottomPad,
                            onBack = { viewingLikedCollection = false },
                            onPlayAll = { vm.playTracks(likedTracks, 0, shuffle = false) },
                            onShuffle = { vm.playTracks(likedTracks, 0, shuffle = true) },
                            onBeamAll = { openBeamWithTracks(likedTracks.map { it.id }.toSet()) },
                            onTrackClick = { idx -> vm.playTracks(likedTracks, idx, shuffle = false) },
                            onToggleLike = vm::toggleLike,
                            onMoreClick = { trackForOptions = it }
                        )
                    }

                    currentTab == MainTab.LIBRARY -> {
                        LibraryScreen(
                            tracks = libraryTracks,
                            totalLibrarySize = tracks.size,
                            likedIds = likedIds,
                            selectedIds = multiSelectIds,
                            filterMode = filterMode,
                            sortOrder = sortOrder,
                            currentTrackId = playback.currentTrack?.id,
                            isPlaying = playback.isPlaying,
                            isImporting = isImporting,
                            accentColor = animAccent,
                            bottomPadding = listBottomPad,
                            onImportClick = { importLauncher.launch(arrayOf("audio/*")) },
                            onReceiveBeamClick = { openBeamWithTracks(emptySet(), BeamModeTab.RECEIVE) },
                            onSortClick = { showSortSheet = true },
                            onSelectFilter = vm::setFilterMode,
                            onPlayAll = { vm.playTracks(libraryTracks, 0, shuffle = false) },
                            onShuffle = { vm.playTracks(libraryTracks, 0, shuffle = true) },
                            onTrackClick = { idx, track ->
                                if (isMultiSelectActive) {
                                    multiSelectIds = if (multiSelectIds.contains(track.id)) {
                                        multiSelectIds - track.id
                                    } else {
                                        multiSelectIds + track.id
                                    }
                                } else {
                                    vm.playTracks(libraryTracks, idx, shuffle = false)
                                }
                            },
                            onTrackLongClick = { track -> multiSelectIds = multiSelectIds + track.id },
                            onCancelMultiSelect = { multiSelectIds = emptySet() },
                            onToggleSelectAll = {
                                multiSelectIds = if (multiSelectIds.size == libraryTracks.size) {
                                    emptySet()
                                } else {
                                    libraryTracks.map { it.id }.toSet()
                                }
                            },
                            onToggleLike = vm::toggleLike,
                            onTrackOptions = { trackForOptions = it }
                        )
                    }

                    currentTab == MainTab.SEARCH -> {
                        SearchScreen(
                            query = searchQuery,
                            results = searchResults,
                            allTracks = tracks,
                            likedIds = likedIds,
                            currentTrackId = playback.currentTrack?.id,
                            isPlaying = playback.isPlaying,
                            accentColor = animAccent,
                            bottomPadding = listBottomPad,
                            onQueryChange = vm::setSearchQuery,
                            onQuickFilter = { filter ->
                                vm.setFilterMode(filter)
                                currentTab = MainTab.LIBRARY
                            },
                            onPlayResults = { idx -> vm.playTracks(searchResults, idx, shuffle = false) },
                            onToggleLike = vm::toggleLike,
                            onTrackOptions = { trackForOptions = it }
                        )
                    }

                    currentTab == MainTab.PLAYLISTS -> {
                        PlaylistsScreen(
                            playlists = playlists,
                            allTracks = tracks,
                            likedCount = likedTracks.size,
                            accentColor = animAccent,
                            bottomPadding = listBottomPad,
                            onCreatePlaylist = { showCreatePlaylistDialog = true },
                            onOpenLiked = { viewingLikedCollection = true },
                            onOpenPlaylist = { openPlaylistId = it.id }
                        )
                    }

                    currentTab == MainTab.BEAM -> {
                        StudioBeamScreen(
                            tracks = tracks,
                            beamState = beamState,
                            activeTab = beamTab,
                            preselectedTrackIds = beamPreselectedIds,
                            accentColor = animAccent,
                            bottomPadding = listBottomPad,
                            onTabChange = { beamTab = it },
                            onSetRequirePin = vm::setBeamRequirePin,
                            onStartSending = vm::startBeamSend,
                            onStopSession = vm::stopBeamSession,
                            onStartScanning = vm::startBeamScanning,
                            onStopScanning = vm::stopBeamScanning,
                            onConnectPeer = vm::connectBeamPeer,
                            onConnectManual = { code, pin ->
                                val ok = vm.connectBeamManual(code, pin)
                                if (!ok) vm.showBanner("Invalid Beam Code or IP:Port")
                            }
                        )
                    }
                }

                // Multi-Select Batch Action Dock
                androidx.compose.animation.AnimatedVisibility(
                    visible = isMultiSelectActive && !showNowPlaying,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E1E26),
                        border = BorderStroke(1.dp, GlassBorder),
                        shadowElevation = 14.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                onClick = {
                                    val chosen = multiSelectIds
                                    multiSelectIds = emptySet()
                                    openBeamWithTracks(chosen, BeamModeTab.SEND)
                                },
                                shape = CircleShape,
                                color = animAccent,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.WifiTethering, contentDescription = null, tint = CanvasBg, modifier = Modifier.size(17.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Beam (${multiSelectIds.size})", color = CanvasBg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            Surface(
                                onClick = {
                                    val chosenList = tracks.filter { multiSelectIds.contains(it.id) }
                                    vm.queueBatchNext(chosenList)
                                    multiSelectIds = emptySet()
                                },
                                shape = CircleShape,
                                color = SurfaceChip,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.QueuePlayNext, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(17.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Play Next", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // Docked Slim Spotify-Style Dynamic Mini-Player (58dp)
                androidx.compose.animation.AnimatedVisibility(
                    visible = playback.currentTrack != null && !showNowPlaying && !isMultiSelectActive,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    playback.currentTrack?.let { current ->
                        DockedSpotifyMiniPlayer(
                            track = current,
                            isPlaying = playback.isPlaying,
                            progress = if (playback.durationMs > 0L) {
                                (playback.positionMs.toFloat() / playback.durationMs.toFloat()).coerceIn(0f, 1f)
                            } else 0f,
                            surfaceTint = lerp(SurfaceElevated, animSecondary, 0.55f),
                            accentColor = animAccent,
                            dspActive = dspState.hasActiveProcessing,
                            onExpand = { showNowPlaying = true },
                            onOpenAudioLab = { showAudioLabSheet = true },
                            onTogglePlayPause = vm::togglePlayPause,
                            onSkipNext = vm::skipNext,
                            onSkipPrev = vm::skipPrevious
                        )
                    }
                }
            }

            // Sleek 4-Tab Spotify Bottom Navigation Bar
            SpotifyBottomNavBar(
                currentTab = currentTab,
                beamActive = beamState.mode == BeamMode.SENDING || beamState.mode == BeamMode.RECEIVING,
                accentColor = animAccent,
                onSelectTab = { tab ->
                    currentTab = tab
                    multiSelectIds = emptySet()
                    if (tab != MainTab.PLAYLISTS) {
                        openPlaylistId = null
                        viewingLikedCollection = false
                    }
                }
            )
        }

        // Subtle Floating Status Toast Pill
        AnimatedVisibility(
            visible = banner != null,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF23232C),
                border = BorderStroke(1.dp, GlassBorder),
                shadowElevation = 12.dp
            ) {
                Text(
                    text = banner.orEmpty(),
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Full-Screen Now Playing Sheet (Touch-Isolated, Swipe-Down Dismiss, Waveform Scrubber)
        AnimatedVisibility(
            visible = showNowPlaying && playback.currentTrack != null,
            enter = slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) { it } + fadeIn(tween(200)),
            exit = slideOutVertically(
                animationSpec = tween(240, easing = FastOutSlowInEasing)
            ) { it } + fadeOut(tween(170))
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
                    onOpenQueue = { showQueueSheet = true },
                    onBeamCurrent = { openBeamWithTracks(setOf(current.id), BeamModeTab.SEND) }
                )
            }
        }
    }

    // Fixed-Height Pro DSP Audio Lab Bottom Sheet (ZERO height jumping between tabs!)
    if (showAudioLabSheet) {
        FixedHeightAudioLabSheet(
            dspState = dspState,
            customPresets = customEqPresets,
            playback = playback,
            accentColor = animAccent,
            onDismiss = { showAudioLabSheet = false },
            onToggleEq = vm::setEqEnabled,
            onSelectPreset = vm::applyEqPreset,
            onSelectCustomPreset = vm::applyCustomEqPreset,
            onSaveCustomPreset = vm::saveCurrentEqAsCustomPreset,
            onDeleteCustomPreset = vm::deleteCustomEqPreset,
            onBandChange = vm::setEqBandGain,
            onBassBoostChange = vm::setBassBoost,
            onPreampChange = vm::setPreampGainDb,
            onVirtualizerChange = vm::setVirtualizerStrength,
            onBalanceChange = vm::setStereoBalance,
            onMonoMixChange = vm::setMonoMix,
            onReverbChange = vm::setReverbPreset,
            onResetStage = vm::resetStageAndRoom,
            onSpeedChange = vm::setSpeed,
            onPitchChange = vm::setPitch,
            onSkipSilenceChange = vm::setSkipSilence,
            onResetSpeedPitch = vm::resetTempoPitch,
            onMarkA = vm::markLoopA,
            onMarkB = vm::markLoopB,
            onToggleAbLoop = vm::toggleAbLoop,
            onClearAbLoop = vm::clearAbLoop,
            onNudgePosition = vm::nudgePosition,
            onSetSleepMinutes = vm::setSleepTimerMinutes,
            onCrossfadeChange = vm::setCrossfadeSeconds
        )
    }

    // Sort Order Picker Sheet
    if (showSortSheet) {
        SortPickerSheet(
            currentSort = sortOrder,
            accentColor = animAccent,
            onDismiss = { showSortSheet = false },
            onSelectSort = {
                vm.setSortOrder(it)
                showSortSheet = false
            }
        )
    }

    // Active Queue Sheet
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
            onBeamTrack = {
                trackForOptions = null
                openBeamWithTracks(setOf(track.id), BeamModeTab.SEND)
            },
            onAddToPlaylist = {
                trackForPlaylistPicker = track
                trackForOptions = null
            },
            onEditInfo = {
                trackForMetadataEdit = track
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

    // Edit Track Metadata Dialog
    trackForMetadataEdit?.let { track ->
        EditTrackInfoDialog(
            track = track,
            onDismiss = { trackForMetadataEdit = null },
            onSave = { title, artist, album ->
                vm.updateTrackMetadata(track.id, title, artist, album)
                trackForMetadataEdit = null
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

// ============================================================================
// BOTTOM NAVIGATION BAR & DOCKED MINI-PLAYER
// ============================================================================

@Composable
private fun SpotifyBottomNavBar(
    currentTab: MainTab,
    beamActive: Boolean,
    accentColor: Color,
    onSelectTab: (MainTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0A0D))
            .navigationBarsPadding()
    ) {
        HorizontalDivider(color = DividerSubtle, thickness = 0.7.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainTab.entries.forEach { tab ->
                val selected = tab == currentTab
                val tint = when {
                    selected -> TextPrimary
                    tab == MainTab.BEAM && beamActive -> accentColor
                    else -> TextMuted
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelectTab(tab) }
                        )
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = tint,
                            modifier = Modifier.size(22.dp)
                        )
                        if (tab == MainTab.BEAM && beamActive) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(accentColor)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.label,
                        color = tint,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun DockedSpotifyMiniPlayer(
    track: AudioTrack,
    isPlaying: Boolean,
    progress: Float,
    surfaceTint: Color,
    accentColor: Color,
    dspActive: Boolean,
    onExpand: () -> Unit,
    onOpenAudioLab: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrev: () -> Unit
) {
    var dragAccumulator by remember { mutableFloatStateOf(0f) }

    Surface(
        onClick = onExpand,
        shape = RoundedCornerShape(10.dp),
        color = surfaceTint,
        shadowElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(track.id) {
                detectHorizontalDragGestures(
                    onDragStart = { dragAccumulator = 0f },
                    onHorizontalDrag = { _, delta -> dragAccumulator += delta },
                    onDragEnd = {
                        when {
                            dragAccumulator < -85f -> onSkipNext()
                            dragAccumulator > 85f -> onSkipPrev()
                        }
                        dragAccumulator = 0f
                    },
                    onDragCancel = { dragAccumulator = 0f }
                )
            }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 8.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TrackArtwork(
                    artworkPath = track.artworkPath,
                    seed = track.id,
                    size = 42.dp,
                    cornerRadius = 6.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        color = TextPrimary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        color = TextSecondary,
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onOpenAudioLab, modifier = Modifier.size(38.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Equalizer,
                        contentDescription = "Audio Lab",
                        tint = if (dspActive) accentColor else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onTogglePlayPause, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play or Pause",
                        tint = TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .height(2.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.16f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(2.dp)
                        .background(TextPrimary)
                )
            }
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}

// ============================================================================
// TAB 1: CLEAN SPOTIFY-GRADE LIBRARY SCREEN
// ============================================================================

@Composable
private fun LibraryScreen(
    tracks: List<AudioTrack>,
    totalLibrarySize: Int,
    likedIds: Set<String>,
    selectedIds: Set<String>,
    filterMode: LibraryFilter,
    sortOrder: LibrarySort,
    currentTrackId: String?,
    isPlaying: Boolean,
    isImporting: Boolean,
    accentColor: Color,
    bottomPadding: Dp,
    onImportClick: () -> Unit,
    onReceiveBeamClick: () -> Unit,
    onSortClick: () -> Unit,
    onSelectFilter: (LibraryFilter) -> Unit,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onTrackClick: (Int, AudioTrack) -> Unit,
    onTrackLongClick: (AudioTrack) -> Unit,
    onCancelMultiSelect: () -> Unit,
    onToggleSelectAll: () -> Unit,
    onToggleLike: (String) -> Unit,
    onTrackOptions: (AudioTrack) -> Unit
) {
    val multiSelectMode = selectedIds.isNotEmpty()

    Column(modifier = Modifier.fillMaxSize()) {
        // Clean Single-Line Header (ZERO wrapping, ZERO subtitle clutter)
        if (multiSelectMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCancelMultiSelect) {
                    Icon(Icons.Rounded.Close, contentDescription = "Cancel", tint = TextPrimary)
                }
                Text(
                    text = "${selectedIds.size} selected",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onToggleSelectAll) {
                    Icon(Icons.Rounded.DoneAll, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (selectedIds.size == tracks.size) "Clear" else "All",
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 12.dp, top = 12.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Library",
                    color = TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSortClick) {
                    Icon(
                        imageVector = Icons.Rounded.SwapVert,
                        contentDescription = "Sort tracks",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = onImportClick, enabled = !isImporting) {
                    if (isImporting) {
                        CircularProgressIndicator(color = TextPrimary, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Import music",
                            tint = TextPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        // Single Slim Filter Chip Row
        if (totalLibrarySize > 0) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(LibraryFilter.entries) { filter ->
                    val active = filter == filterMode
                    Surface(
                        onClick = { onSelectFilter(filter) },
                        shape = CircleShape,
                        color = if (active) accentColor else SurfaceChip
                    ) {
                        Text(
                            text = filter.label,
                            color = if (active) CanvasBg else TextPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Track List or Clean Empty State
        if (tracks.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (totalLibrarySize == 0) "Your library is empty" else "No matching tracks",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (totalLibrarySize == 0) {
                        "Import local FLAC, MP3, WAV, or M4A tracks—or receive music directly with StudioBeam."
                    } else {
                        "Try switching to All tracks above."
                    },
                    color = TextSecondary,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )
                if (totalLibrarySize == 0) {
                    Spacer(modifier = Modifier.height(22.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            onClick = onImportClick,
                            shape = CircleShape,
                            color = TextPrimary
                        ) {
                            Text(
                                text = "Import Music",
                                color = CanvasBg,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 11.dp)
                            )
                        }
                        Surface(
                            onClick = onReceiveBeamClick,
                            shape = CircleShape,
                            color = SurfaceChip
                        ) {
                            Text(
                                text = "Receive Beam",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp)
                            )
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = bottomPadding)
            ) {
                // Compact Spotify-Style Action Strip (replaces giant Play/Shuffle boxes)
                if (!multiSelectMode) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 18.dp, end = 16.dp, top = 6.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${tracks.size} song${if (tracks.size == 1) "" else "s"} • ${sortOrder.label}",
                                color = TextSecondary,
                                fontSize = 12.5.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = onShuffle, modifier = Modifier.size(40.dp)) {
                                Icon(
                                    imageVector = Icons.Rounded.Shuffle,
                                    contentDescription = "Shuffle",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                onClick = onPlayAll,
                                shape = CircleShape,
                                color = accentColor,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.PlayArrow,
                                        contentDescription = "Play All",
                                        tint = CanvasBg,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                itemsIndexed(tracks, key = { _, t -> t.id }) { index, track ->
                    val isCurrent = track.id == currentTrackId
                    val isSelected = selectedIds.contains(track.id)
                    FlatTrackRow(
                        track = track,
                        isCurrent = isCurrent,
                        isPlaying = isCurrent && isPlaying,
                        isLiked = likedIds.contains(track.id),
                        multiSelectMode = multiSelectMode,
                        isSelected = isSelected,
                        accentColor = accentColor,
                        onClick = { onTrackClick(index, track) },
                        onLongClick = { onTrackLongClick(track) },
                        onToggleLike = { onToggleLike(track.id) },
                        onMoreClick = { onTrackOptions(track) }
                    )
                }
            }
        }
    }
}

/**
 * Clean, borderless Spotify-style edge-to-edge track row.
 * Zero glossy cards or white borders competing with the album art.
 */
@Composable
private fun FlatTrackRow(
    track: AudioTrack,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isLiked: Boolean,
    multiSelectMode: Boolean,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onToggleLike: () -> Unit,
    onMoreClick: () -> Unit
) {
    val rowBg = when {
        isSelected -> accentColor.copy(alpha = 0.14f)
        isCurrent -> Color.White.copy(alpha = 0.04f)
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(start = 18.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (multiSelectMode) {
            Icon(
                imageVector = if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) accentColor else TextMuted,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(22.dp)
            )
        }

        Box(contentAlignment = Alignment.Center) {
            TrackArtwork(
                artworkPath = track.artworkPath,
                seed = track.id,
                size = 48.dp,
                cornerRadius = 6.dp
            )
            if (isCurrent) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.52f)),
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
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (track.isLossless) {
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = track.codec,
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "${track.artist} • ${formatDuration(track.durationMs)}",
                    color = TextSecondary,
                    fontSize = 12.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (!multiSelectMode) {
            if (isLiked) {
                IconButton(onClick = onToggleLike, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = "Liked",
                        tint = accentColor,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
            IconButton(onClick = onMoreClick, modifier = Modifier.size(38.dp)) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "More options",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ============================================================================
// TAB 2: DEDICATED SPOTIFY-STYLE SEARCH SCREEN
// ============================================================================

@Composable
private fun SearchScreen(
    query: String,
    results: List<AudioTrack>,
    allTracks: List<AudioTrack>,
    likedIds: Set<String>,
    currentTrackId: String?,
    isPlaying: Boolean,
    accentColor: Color,
    bottomPadding: Dp,
    onQueryChange: (String) -> Unit,
    onQuickFilter: (LibraryFilter) -> Unit,
    onPlayResults: (Int) -> Unit,
    onToggleLike: (String) -> Unit,
    onTrackOptions: (AudioTrack) -> Unit
) {
    val topArtists = remember(allTracks) {
        allTracks
            .map { it.artist }
            .filter { it.isNotBlank() && it != "Unknown Artist" }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(8)
            .map { it.key }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Search",
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
            maxLines = 1,
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 10.dp)
        )

        // Slim 42dp Spotify-style Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .height(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF24242B))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (query.isEmpty()) {
                    Text(
                        text = "Songs, artists, albums, or FLAC",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(color = TextPrimary, fontSize = 14.sp),
                    cursorBrush = SolidColor(accentColor),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Clear",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onQueryChange("") }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (query.isBlank()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = bottomPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Browse your music", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        BrowseCategoryTile(
                            title = "Liked Songs",
                            subtitle = "${likedIds.size} tracks",
                            bgColor = Color(0xFF3B2878),
                            onClick = { onQuickFilter(LibraryFilter.FAVORITES) },
                            modifier = Modifier.weight(1f)
                        )
                        BrowseCategoryTile(
                            title = "Lossless",
                            subtitle = "${allTracks.count { it.isLossless }} Hi-Res",
                            bgColor = Color(0xFF1A4F48),
                            onClick = { onQuickFilter(LibraryFilter.LOSSLESS) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        BrowseCategoryTile(
                            title = "Recently Played",
                            subtitle = "${allTracks.count { it.lastPlayedAt > 0L }} played",
                            bgColor = Color(0xFF6B2D3E),
                            onClick = { onQuickFilter(LibraryFilter.RECENTLY_PLAYED) },
                            modifier = Modifier.weight(1f)
                        )
                        BrowseCategoryTile(
                            title = "All Tracks",
                            subtitle = "${allTracks.size} total",
                            bgColor = Color(0xFF283E5C),
                            onClick = { onQuickFilter(LibraryFilter.ALL) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (topArtists.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Artists in your library", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(topArtists) { artist ->
                                Surface(
                                    onClick = { onQueryChange(artist) },
                                    shape = CircleShape,
                                    color = SurfaceChip
                                ) {
                                    Text(
                                        text = artist,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else if (results.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No matches for \"$query\"", color = TextSecondary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = bottomPadding)
            ) {
                itemsIndexed(results, key = { _, t -> t.id }) { index, track ->
                    val isCurrent = track.id == currentTrackId
                    FlatTrackRow(
                        track = track,
                        isCurrent = isCurrent,
                        isPlaying = isCurrent && isPlaying,
                        isLiked = likedIds.contains(track.id),
                        multiSelectMode = false,
                        isSelected = false,
                        accentColor = accentColor,
                        onClick = { onPlayResults(index) },
                        onLongClick = {},
                        onToggleLike = { onToggleLike(track.id) },
                        onMoreClick = { onTrackOptions(track) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BrowseCategoryTile(
    title: String,
    subtitle: String,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        modifier = modifier.height(78.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(subtitle, color = Color.White.copy(alpha = 0.78f), fontSize = 12.sp, maxLines = 1)
        }
    }
}

// ============================================================================
// TAB 3: PLAYLISTS & SPOTIFY-STYLE LIKED SONGS
// ============================================================================

@Composable
private fun PlaylistsScreen(
    playlists: List<UserPlaylist>,
    allTracks: List<AudioTrack>,
    likedCount: Int,
    accentColor: Color,
    bottomPadding: Dp,
    onCreatePlaylist: () -> Unit,
    onOpenLiked: () -> Unit,
    onOpenPlaylist: (UserPlaylist) -> Unit
) {
    val trackMap = remember(allTracks) { allTracks.associateBy { it.id } }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 12.dp, top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Playlists",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onCreatePlaylist) {
                Icon(Icons.Rounded.Add, contentDescription = "Create playlist", tint = TextPrimary, modifier = Modifier.size(26.dp))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = bottomPadding)
        ) {
            // Pinned Spotify-style Liked Songs Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenLiked)
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF4C35DE), Color(0xFF8C7AE6))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Liked Songs", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text("Playlist • $likedCount song${if (likedCount == 1) "" else "s"}", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }

            items(playlists, key = { it.id }) { playlist ->
                val coverTrack = playlist.trackIds.firstOrNull()?.let { trackMap[it] }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenPlaylist(playlist) }
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TrackArtwork(
                        artworkPath = coverTrack?.artworkPath,
                        seed = playlist.id,
                        size = 56.dp,
                        cornerRadius = 8.dp
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
                        Text(
                            text = "Playlist • ${playlist.trackIds.size} song${if (playlist.trackIds.size == 1) "" else "s"}",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            if (playlists.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No custom playlists yet", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Tap + at the top right to create a playlist.", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun LikedCollectionScreen(
    tracks: List<AudioTrack>,
    currentTrackId: String?,
    isPlaying: Boolean,
    accentColor: Color,
    bottomPadding: Dp,
    onBack: () -> Unit,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onBeamAll: () -> Unit,
    onTrackClick: (Int) -> Unit,
    onToggleLike: (String) -> Unit,
    onMoreClick: (AudioTrack) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Liked Songs", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("${tracks.size} songs", color = TextSecondary, fontSize = 12.sp)
            }
            if (tracks.isNotEmpty()) {
                IconButton(onClick = onBeamAll) {
                    Icon(Icons.Rounded.WifiTethering, contentDescription = "Beam Liked Songs", tint = accentColor)
                }
                IconButton(onClick = onShuffle) {
                    Icon(Icons.Rounded.Shuffle, contentDescription = "Shuffle", tint = TextSecondary)
                }
                Surface(
                    onClick = onPlayAll,
                    shape = CircleShape,
                    color = accentColor,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = "Play", tint = CanvasBg, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }

        if (tracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Songs you like will appear here.", color = TextSecondary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = bottomPadding)
            ) {
                itemsIndexed(tracks, key = { _, t -> t.id }) { idx, track ->
                    val isCurrent = track.id == currentTrackId
                    FlatTrackRow(
                        track = track,
                        isCurrent = isCurrent,
                        isPlaying = isCurrent && isPlaying,
                        isLiked = true,
                        multiSelectMode = false,
                        isSelected = false,
                        accentColor = accentColor,
                        onClick = { onTrackClick(idx) },
                        onLongClick = {},
                        onToggleLike = { onToggleLike(track.id) },
                        onMoreClick = { onMoreClick(track) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistDetailScreen(
    playlist: UserPlaylist,
    tracks: List<AudioTrack>,
    currentTrackId: String?,
    isPlaying: Boolean,
    accentColor: Color,
    bottomPadding: Dp,
    onBack: () -> Unit,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onBeamPlaylist: () -> Unit,
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
                .padding(horizontal = 8.dp, vertical = 8.dp),
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
                Text("${tracks.size} songs", color = TextSecondary, fontSize = 12.sp)
            }
            if (tracks.isNotEmpty()) {
                IconButton(onClick = onBeamPlaylist) {
                    Icon(Icons.Rounded.WifiTethering, contentDescription = "Beam Playlist", tint = accentColor)
                }
            }
            IconButton(onClick = onDeletePlaylist) {
                Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete playlist", tint = Color(0xFFFF6B6B))
            }
        }

        if (tracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Add songs from Library using the ⋮ menu.", color = TextSecondary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = bottomPadding)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 18.dp, end = 16.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Use arrows to reorder", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = onShuffle) {
                            Icon(Icons.Rounded.Shuffle, contentDescription = "Shuffle", tint = TextSecondary)
                        }
                        Surface(
                            onClick = onPlayAll,
                            shape = CircleShape,
                            color = accentColor,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = "Play", tint = CanvasBg, modifier = Modifier.size(25.dp))
                            }
                        }
                    }
                }
                itemsIndexed(tracks, key = { idx, t -> "${t.id}_$idx" }) { index, track ->
                    val isCurrent = track.id == currentTrackId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isCurrent) Color.White.copy(alpha = 0.04f) else Color.Transparent)
                            .clickable { onTrackClick(index) }
                            .padding(start = 18.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            TrackArtwork(track.artworkPath, track.id, 46.dp, 6.dp)
                            if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AcousticPulseBars(isPlaying, accentColor, 3, 16.dp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                color = if (isCurrent) accentColor else TextPrimary,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(track.artist, color = TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        if (index > 0) {
                            IconButton(onClick = { onMoveUp(index) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Rounded.ArrowUpward, contentDescription = "Up", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                        if (index < tracks.lastIndex) {
                            IconButton(onClick = { onMoveDown(index) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Rounded.ArrowDownward, contentDescription = "Down", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                        IconButton(onClick = { onRemoveTrack(track.id) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Rounded.Close, contentDescription = "Remove", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// TAB 4: DEDICATED STUDIOBEAM (ASP-v1) SCREEN
// ============================================================================

@Composable
private fun StudioBeamScreen(
    tracks: List<AudioTrack>,
    beamState: StudioBeamUiState,
    activeTab: BeamModeTab,
    preselectedTrackIds: Set<String>,
    accentColor: Color,
    bottomPadding: Dp,
    onTabChange: (BeamModeTab) -> Unit,
    onSetRequirePin: (Boolean) -> Unit,
    onStartSending: (Set<String>) -> Unit,
    onStopSession: () -> Unit,
    onStartScanning: () -> Unit,
    onStopScanning: () -> Unit,
    onConnectPeer: (DiscoveredBeamPeer, String) -> Unit,
    onConnectManual: (String, String) -> Unit
) {
    var selectedToSend by remember(preselectedTrackIds) { mutableStateOf(preselectedTrackIds) }
    var manualCode by remember { mutableStateOf("") }
    var manualPin by remember { mutableStateOf("") }
    var peerAwaitingPin by remember { mutableStateOf<DiscoveredBeamPeer?>(null) }

    LaunchedEffect(activeTab, beamState.mode) {
        if (activeTab == BeamModeTab.RECEIVE && beamState.mode == BeamMode.IDLE) {
            onStartScanning()
        } else if (activeTab == BeamModeTab.SEND) {
            onStopScanning()
        }
    }

    DisposableEffect(Unit) {
        onDispose { onStopScanning() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = bottomPadding)
    ) {
        Text(
            text = "StudioBeam",
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
            maxLines = 1
        )
        Text(
            text = "Direct Wi-Fi / Hotspot music transfer • Zero internet data",
            color = TextSecondary,
            fontSize = 12.5.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Send / Receive Segmented Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceElevated)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BeamModeTab.entries.forEach { tab ->
                val selected = tab == activeTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) SurfaceChip else Color.Transparent)
                        .clickable {
                            if (beamState.mode != BeamMode.SENDING && beamState.mode != BeamMode.RECEIVING) {
                                onTabChange(tab)
                            }
                        }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        color = if (selected) TextPrimary else TextSecondary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (beamState.mode != BeamMode.IDLE) {
            BeamActiveStatusCard(
                beamState = beamState,
                accentColor = accentColor,
                onStopOrReset = onStopSession
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        when (activeTab) {
            BeamModeTab.SEND -> {
                if (beamState.mode != BeamMode.SENDING) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select songs (${selectedToSend.size}/${tracks.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        TextButton(
                            onClick = {
                                selectedToSend = if (selectedToSend.size == tracks.size) {
                                    emptySet()
                                } else {
                                    tracks.map { it.id }.toSet()
                                }
                            }
                        ) {
                            Text(
                                text = if (selectedToSend.size == tracks.size) "Clear All" else "Select All",
                                color = accentColor,
                                fontSize = 12.5.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceElevated)
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Lock, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Require 4-digit PIN", fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
                            Text("Recommended on public Wi-Fi", color = TextMuted, fontSize = 11.5.sp)
                        }
                        Switch(
                            checked = beamState.requirePin,
                            onCheckedChange = onSetRequirePin,
                            colors = SwitchDefaults.colors(checkedTrackColor = accentColor, checkedThumbColor = CanvasBg)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (tracks.isEmpty()) {
                        Text(
                            text = "Import songs into your Library first to send them.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            tracks.forEach { track ->
                                val checked = selectedToSend.contains(track.id)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (checked) accentColor.copy(alpha = 0.14f) else Color.Transparent)
                                        .clickable {
                                            selectedToSend = if (checked) selectedToSend - track.id else selectedToSend + track.id
                                        }
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (checked) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (checked) accentColor else TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    TrackArtwork(track.artworkPath, track.id, 38.dp, 5.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(track.title, color = TextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("${track.artist} • ${track.codec}", color = TextSecondary, fontSize = 11.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        onClick = { onStartSending(selectedToSend) },
                        enabled = selectedToSend.isNotEmpty(),
                        shape = CircleShape,
                        color = if (selectedToSend.isNotEmpty()) accentColor else SurfaceChip,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 13.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Rounded.WifiTethering,
                                contentDescription = null,
                                tint = if (selectedToSend.isNotEmpty()) CanvasBg else TextMuted,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start StudioBeam (${selectedToSend.size})",
                                color = if (selectedToSend.isNotEmpty()) CanvasBg else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            BeamModeTab.RECEIVE -> {
                if (beamState.mode != BeamMode.RECEIVING) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Sensors, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (beamState.isScanning) "Scanning nearby devices…" else "Nearby senders",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (beamState.isScanning) {
                            CircularProgressIndicator(color = accentColor, strokeWidth = 2.dp, modifier = Modifier.size(15.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (beamState.discoveredPeers.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceElevated)
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Waiting for sender", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Connect both phones to the same Wi-Fi or Mobile Hotspot and tap Start StudioBeam on the sender.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            beamState.discoveredPeers.forEach { peer ->
                                Surface(
                                    onClick = {
                                        if (peer.requiresPin) peerAwaitingPin = peer else onConnectPeer(peer, "")
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = SurfaceElevated,
                                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Rounded.WifiTethering, contentDescription = null, tint = accentColor)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(peer.deviceName, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                "${peer.trackCount} songs • ${formatBytes(peer.totalBytes)}${if (peer.requiresPin) " • PIN" else ""}",
                                                color = TextSecondary,
                                                fontSize = 12.sp
                                            )
                                        }
                                        Text("Receive", color = accentColor, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = DividerSubtle)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Manual Beam Code or IP:Port", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = manualCode,
                            onValueChange = { manualCode = it },
                            singleLine = true,
                            placeholder = { Text("XXXXX-XXXXX", color = TextMuted, fontSize = 13.sp) },
                            modifier = Modifier.weight(1.7f)
                        )
                        OutlinedTextField(
                            value = manualPin,
                            onValueChange = { if (it.length <= 4) manualPin = it },
                            singleLine = true,
                            placeholder = { Text("PIN", color = TextMuted, fontSize = 13.sp) },
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        onClick = { onConnectManual(manualCode, manualPin) },
                        enabled = manualCode.isNotBlank(),
                        shape = CircleShape,
                        color = if (manualCode.isNotBlank()) TextPrimary else SurfaceChip,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 12.dp)) {
                            Text(
                                "Connect & Import",
                                color = if (manualCode.isNotBlank()) CanvasBg else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        }
                    }
                }
            }
        }
    }

    peerAwaitingPin?.let { peer ->
        var enteredPin by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { peerAwaitingPin = null },
            containerColor = SurfaceSheet,
            title = { Text("Enter 4-Digit PIN", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Connecting to ${peer.deviceName}", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = { if (it.length <= 4) enteredPin = it },
                        singleLine = true,
                        placeholder = { Text("4-digit PIN") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConnectPeer(peer, enteredPin)
                        peerAwaitingPin = null
                    }
                ) {
                    Text("Connect", color = accentColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { peerAwaitingPin = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun BeamActiveStatusCard(
    beamState: StudioBeamUiState,
    accentColor: Color,
    onStopOrReset: () -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "radarPulse")
    val pulse by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarRing"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceElevated)
            .border(1.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (beamState.isBroadcasting) {
            Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = accentColor.copy(alpha = (1f - pulse) * 0.65f),
                        radius = size.minDimension * 0.5f * pulse,
                        style = Stroke(width = 3f)
                    )
                }
                Icon(Icons.Rounded.WifiTethering, contentDescription = null, tint = accentColor, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Text(
            text = beamState.errorMessage ?: beamState.statusText,
            color = if (beamState.mode == BeamMode.ERROR) Color(0xFFFF6B6B) else TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (beamState.currentTrackTitle.isNotBlank() && (beamState.mode == BeamMode.SENDING || beamState.mode == BeamMode.RECEIVING)) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(beamState.currentTrackTitle, color = accentColor, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        if (beamState.mode == BeamMode.SENDING && beamState.beamCode.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("BEAM CODE", color = TextMuted, fontSize = 10.sp, letterSpacing = 1.4.sp, fontWeight = FontWeight.Bold)
            Text(
                text = beamState.beamCode,
                color = TextPrimary,
                fontSize = 21.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "${beamState.localIp}:${beamState.activePort}${if (beamState.requirePin) "  •  PIN: ${beamState.sessionPin}" else ""}",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        if (beamState.totalTracks > 0 && beamState.mode != BeamMode.ERROR) {
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { beamState.progressFraction },
                color = accentColor,
                trackColor = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${beamState.completedTracks} / ${beamState.totalTracks} songs", color = TextSecondary, fontSize = 11.5.sp)
                Text(
                    text = if (beamState.speedMbps > 0.05f) {
                        String.format(Locale.US, "%.1f MB/s • %s", beamState.speedMbps, formatBytes(beamState.bytesTransferred))
                    } else {
                        formatBytes(beamState.bytesTransferred)
                    },
                    color = accentColor,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        TextButton(onClick = onStopOrReset) {
            Text(
                text = if (beamState.mode == BeamMode.COMPLETED || beamState.mode == BeamMode.ERROR) "Done" else "Stop StudioBeam",
                color = Color(0xFFFF6B6B),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ============================================================================
// FULL-SCREEN NOW PLAYING (REFINED SPOTIFY GRADIENT + WAVEFORM SCRUBBER)
// ============================================================================

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
    onOpenQueue: () -> Unit,
    onBeamCurrent: () -> Unit
) {
    val artworkScale by animateFloatAsState(
        targetValue = if (playback.isPlaying) 1.0f else 0.88f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "artworkSpringScale"
    )

    var isScrubbing by remember { mutableStateOf(false) }
    var scrubValue by remember { mutableFloatStateOf(0f) }
    var pullDownOffsetPx by remember { mutableFloatStateOf(0f) }
    var artworkSwipeDrag by remember { mutableFloatStateOf(0f) }

    val duration = playback.durationMs.coerceAtLeast(1L)
    val displayedPositionMs = if (isScrubbing) (scrubValue * duration).toLong() else playback.positionMs.coerceIn(0L, duration)
    val sliderFraction = if (isScrubbing) scrubValue else (playback.positionMs.toFloat() / duration.toFloat()).coerceIn(0f, 1f)

    val loopStartFrac = if (dspState.abLoop.enabled && dspState.abLoop.trackId == track.id) {
        dspState.abLoop.pointAMs?.let { (it.toFloat() / duration.toFloat()).coerceIn(0f, 1f) }
    } else null
    val loopEndFrac = if (dspState.abLoop.enabled && dspState.abLoop.trackId == track.id) {
        dspState.abLoop.pointBMs?.let { (it.toFloat() / duration.toFloat()).coerceIn(0f, 1f) }
    } else null

    val topBackdrop = lerp(CanvasBg, primaryColor, 0.42f)
    val midBackdrop = lerp(CanvasBg, secondaryColor, 0.35f)
    val touchBarrier = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { translationY = pullDownOffsetPx.coerceAtLeast(0f) }
            .background(
                Brush.verticalGradient(
                    0.0f to topBackdrop,
                    0.46f to midBackdrop,
                    0.82f to CanvasBg,
                    1.0f to CanvasBg
                )
            )
            .clickable(interactionSource = touchBarrier, indication = null, onClick = {})
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, delta ->
                        pullDownOffsetPx = (pullDownOffsetPx + delta).coerceIn(0f, 420f)
                    },
                    onDragEnd = {
                        if (pullDownOffsetPx > 140f) onCollapse()
                        pullDownOffsetPx = 0f
                    },
                    onDragCancel = { pullDownOffsetPx = 0f }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
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
                        text = "PLAYING FROM LIBRARY",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = track.album,
                        color = TextPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Top-Right Interactive Wave Pill -> Opens Audio Lab cleanly
                Surface(
                    onClick = onOpenAudioLab,
                    shape = CircleShape,
                    color = if (dspState.hasActiveProcessing) accentColor.copy(alpha = 0.24f) else GlassDock,
                    border = BorderStroke(1.dp, if (dspState.hasActiveProcessing) accentColor.copy(alpha = 0.6f) else GlassBorder)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
                        AcousticPulseBars(
                            isPlaying = playback.isPlaying,
                            barColor = accentColor,
                            barCount = 4,
                            maxHeight = 15.dp,
                            speedMultiplier = dspState.speed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Hero Artwork
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .aspectRatio(1f)
                    .graphicsLayer {
                        scaleX = artworkScale
                        scaleY = artworkScale
                    }
                    .shadow(
                        elevation = if (playback.isPlaying) 28.dp else 10.dp,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .pointerInput(track.id) {
                        detectHorizontalDragGestures(
                            onDragStart = { artworkSwipeDrag = 0f },
                            onHorizontalDrag = { _, delta -> artworkSwipeDrag += delta },
                            onDragEnd = {
                                when {
                                    artworkSwipeDrag < -110f -> onSkipNext()
                                    artworkSwipeDrag > 110f -> onSkipPrev()
                                }
                                artworkSwipeDrag = 0f
                            },
                            onDragCancel = { artworkSwipeDrag = 0f }
                        )
                    }
            ) {
                TrackArtwork(
                    artworkPath = track.artworkPath,
                    seed = track.id,
                    size = null,
                    cornerRadius = 14.dp,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtle Audiophile Codec Telemetry Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.HighQuality,
                    contentDescription = null,
                    tint = if (track.isLossless) accentColor else TextSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = track.qualityBadge,
                    color = if (track.isLossless) accentColor else TextSecondary,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.weight(0.55f))

            // Track Info & Heart
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
                    Spacer(modifier = Modifier.height(2.dp))
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
                        contentDescription = "Like",
                        tint = if (isLiked) accentColor else TextSecondary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sculpted Interactive Waveform Scrubber
            WaveformScrubber(
                seed = track.id,
                progress = sliderFraction,
                activeColor = TextPrimary,
                accentColor = accentColor,
                loopStart = loopStartFrac,
                loopEnd = loopEndFrac,
                onScrubChange = { frac ->
                    isScrubbing = true
                    scrubValue = frac
                },
                onScrubFinished = { frac ->
                    onSeek((frac * duration).toLong())
                    isScrubbing = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatDuration(displayedPositionMs), color = TextSecondary, fontSize = 11.5.sp)
                if (dspState.abLoop.enabled && dspState.abLoop.isReady) {
                    Text(
                        text = "A–B ${formatDuration(dspState.abLoop.pointAMs ?: 0L)}–${formatDuration(dspState.abLoop.pointBMs ?: 0L)}",
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(formatDuration(playback.durationMs), color = TextSecondary, fontSize = 11.5.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Clean Transport Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleShuffle) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playback.shuffleEnabled) accentColor else TextSecondary,
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
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (playback.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = "Play or Pause",
                            tint = CanvasBg,
                            modifier = Modifier.size(36.dp)
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
                        else -> Icons.Rounded.Repeat to TextSecondary
                    }
                    Icon(icon, contentDescription = "Repeat", tint = tint, modifier = Modifier.size(22.dp))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bottom Floating Utility Row (Audio Lab • Beam • Queue)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onOpenAudioLab,
                    shape = CircleShape,
                    color = if (dspState.hasActiveProcessing) accentColor.copy(alpha = 0.20f) else GlassDock,
                    border = BorderStroke(1.dp, if (dspState.hasActiveProcessing) accentColor.copy(alpha = 0.5f) else GlassBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Tune,
                            contentDescription = null,
                            tint = if (dspState.hasActiveProcessing) accentColor else TextPrimary,
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            text = if (dspState.sleepTimerRemainingMs > 0L) {
                                "Lab • ${formatDuration(dspState.sleepTimerRemainingMs)}"
                            } else {
                                "Audio Lab"
                            },
                            color = TextPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onBeamCurrent) {
                        Icon(Icons.Rounded.WifiTethering, contentDescription = "Beam Song", tint = TextSecondary, modifier = Modifier.size(21.dp))
                    }
                    IconButton(onClick = onOpenQueue) {
                        Icon(Icons.AutoMirrored.Rounded.QueueMusic, contentDescription = "Queue", tint = TextSecondary, modifier = Modifier.size(23.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun WaveformScrubber(
    seed: String,
    progress: Float,
    activeColor: Color,
    accentColor: Color,
    loopStart: Float?,
    loopEnd: Float?,
    onScrubChange: (Float) -> Unit,
    onScrubFinished: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val barCount = 46
    val barAmplitudes = remember(seed) {
        val rng = Random(seed.hashCode())
        List(barCount) { idx ->
            val arch = sin((idx.toFloat() / (barCount - 1)) * PI).toFloat()
            val organic = 0.24f + rng.nextFloat() * 0.56f
            (organic * (0.62f + 0.38f * arch)).coerceIn(0.18f, 1.0f)
        }
    }
    var latestDragFraction by remember { mutableFloatStateOf(progress) }

    Canvas(
        modifier = modifier
            .pointerInput(seed) {
                detectTapGestures { offset ->
                    val frac = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                    onScrubFinished(frac)
                }
            }
            .pointerInput(seed) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        latestDragFraction = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                        onScrubChange(latestDragFraction)
                    },
                    onHorizontalDrag = { change, _ ->
                        change.consume()
                        latestDragFraction = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                        onScrubChange(latestDragFraction)
                    },
                    onDragEnd = { onScrubFinished(latestDragFraction) },
                    onDragCancel = { onScrubFinished(latestDragFraction) }
                )
            }
    ) {
        val totalW = size.width
        val totalH = size.height
        val slotW = totalW / barCount
        val barW = (slotW * 0.54f).coerceAtLeast(2.5f)
        val clampedProgress = progress.coerceIn(0f, 1f)

        if (loopStart != null && loopEnd != null && loopEnd > loopStart) {
            val startX = totalW * loopStart
            val endX = totalW * loopEnd
            drawRoundRect(
                color = accentColor.copy(alpha = 0.18f),
                topLeft = Offset(startX, 2f),
                size = Size((endX - startX).coerceAtLeast(4f), totalH - 4f),
                cornerRadius = CornerRadius(10f, 10f)
            )
        }

        barAmplitudes.forEachIndexed { idx, amp ->
            val barFrac = idx.toFloat() / (barCount - 1)
            val barH = (totalH * 0.85f * amp).coerceAtLeast(6f)
            val x = idx * slotW + (slotW - barW) / 2f
            val y = (totalH - barH) / 2f

            val inLoop = loopStart != null && loopEnd != null && barFrac in loopStart..loopEnd
            val color = when {
                barFrac <= clampedProgress -> if (inLoop) accentColor else activeColor
                inLoop -> accentColor.copy(alpha = 0.45f)
                else -> Color.White.copy(alpha = 0.22f)
            }

            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barW, barH),
                cornerRadius = CornerRadius(barW / 2f, barW / 2f)
            )
        }

        val playX = (totalW * clampedProgress).coerceIn(2f, totalW - 2f)
        drawRoundRect(
            color = accentColor,
            topLeft = Offset(playX - 1.5f, 0f),
            size = Size(3f, totalH),
            cornerRadius = CornerRadius(3f, 3f)
        )
    }
}

// ============================================================================
// FIXED-HEIGHT PRO DSP AUDIO LAB SHEET (ZERO HEIGHT JUMPING!)
// ============================================================================

@Composable
private fun FixedHeightAudioLabSheet(
    dspState: DspState,
    customPresets: List<CustomEqPreset>,
    playback: PlaybackUiState,
    accentColor: Color,
    onDismiss: () -> Unit,
    onToggleEq: (Boolean) -> Unit,
    onSelectPreset: (EqPreset) -> Unit,
    onSelectCustomPreset: (CustomEqPreset) -> Unit,
    onSaveCustomPreset: (String) -> Unit,
    onDeleteCustomPreset: (String) -> Unit,
    onBandChange: (Int, Float) -> Unit,
    onBassBoostChange: (Int) -> Unit,
    onPreampChange: (Float) -> Unit,
    onVirtualizerChange: (Int) -> Unit,
    onBalanceChange: (Float) -> Unit,
    onMonoMixChange: (Boolean) -> Unit,
    onReverbChange: (ReverbPreset) -> Unit,
    onResetStage: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onPitchChange: (Float) -> Unit,
    onSkipSilenceChange: (Boolean) -> Unit,
    onResetSpeedPitch: () -> Unit,
    onMarkA: () -> Unit,
    onMarkB: () -> Unit,
    onToggleAbLoop: (Boolean) -> Unit,
    onClearAbLoop: () -> Unit,
    onNudgePosition: (Long) -> Unit,
    onSetSleepMinutes: (Int) -> Unit,
    onCrossfadeChange: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var activeTab by remember { mutableStateOf(AudioLabTab.EQ) }
    var showSavePresetDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceSheet,
        contentColor = TextPrimary
    ) {
        // FIXED 76% HEIGHT CONTAINER -> Switching tabs NEVER causes vertical bouncing!
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.76f)
                .padding(horizontal = 20.dp)
        ) {
            // Pinned Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Audio Lab", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                when (activeTab) {
                    AudioLabTab.EQ -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (dspState.eqEnabled) "On" else "Off", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = dspState.eqEnabled,
                                onCheckedChange = onToggleEq,
                                colors = SwitchDefaults.colors(checkedTrackColor = accentColor, checkedThumbColor = CanvasBg)
                            )
                        }
                    }
                    AudioLabTab.STAGE -> {
                        TextButton(onClick = onResetStage) {
                            Text("Reset", color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    AudioLabTab.SPEED -> {
                        TextButton(onClick = onResetSpeedPitch) {
                            Text("Reset 1.0x", color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    AudioLabTab.FLOW -> {
                        Text(
                            text = if (dspState.sleepTimerRemainingMs > 0L) formatDuration(dspState.sleepTimerRemainingMs) else "Gapless Ready",
                            color = if (dspState.sleepTimerRemainingMs > 0L) accentColor else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pinned 4-Tab Bar (EQ • Stage • Speed • Flow)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceElevated)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AudioLabTab.entries.forEach { tab ->
                    val selected = tab == activeTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) SurfaceChip else Color.Transparent)
                            .clickable { activeTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.label,
                            color = if (selected) TextPrimary else TextSecondary,
                            fontSize = 12.5.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Internal Scrollable Body filling remaining fixed height
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                when (activeTab) {
                    AudioLabTab.EQ -> {
                        // Presets + Save Custom Preset Pill
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val builtIns = listOf(
                                EqPreset.FLAT,
                                EqPreset.BASS_BOOST,
                                EqPreset.VOCAL_CLARITY,
                                EqPreset.LATE_NIGHT,
                                EqPreset.STUDIO_AIR
                            )
                            items(builtIns) { preset ->
                                val selected = dspState.eqEnabled &&
                                    dspState.activeCustomPresetId == null &&
                                    dspState.activePreset == preset
                                Surface(
                                    onClick = { onSelectPreset(preset) },
                                    shape = CircleShape,
                                    color = if (selected) accentColor else SurfaceChip
                                ) {
                                    Text(
                                        text = preset.label,
                                        color = if (selected) CanvasBg else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp)
                                    )
                                }
                            }
                            items(customPresets, key = { it.id }) { custom ->
                                val selected = dspState.eqEnabled && dspState.activeCustomPresetId == custom.id
                                Surface(
                                    onClick = { onSelectCustomPreset(custom) },
                                    shape = CircleShape,
                                    color = if (selected) accentColor else SurfaceChip
                                ) {
                                    Row(
                                        modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 7.dp, bottom = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = custom.name,
                                            color = if (selected) CanvasBg else TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = "Delete preset",
                                            tint = if (selected) CanvasBg.copy(alpha = 0.7f) else TextMuted,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable { onDeleteCustomPreset(custom.id) }
                                        )
                                    }
                                }
                            }
                            item {
                                Surface(
                                    onClick = { showSavePresetDialog = true },
                                    shape = CircleShape,
                                    color = Color.Transparent,
                                    border = BorderStroke(1.dp, GlassBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(Icons.Rounded.Add, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                                        Text("Save", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Real-Time Bezier Frequency Response Curve
                        EqFrequencyCurveGraph(
                            bandGainsDb = dspState.bandGainsDb,
                            enabled = dspState.eqEnabled,
                            accentColor = accentColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(86.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 5 Vertical Mixing-Console Faders (double-tap any fader to reset to 0dB)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(172.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StudioEngineBridge.bandLabels.forEachIndexed { idx, freq ->
                                val gain = dspState.bandGainsDb.getOrElse(idx) { 0f }
                                VerticalEqFader(
                                    label = freq,
                                    gainDb = gain,
                                    enabled = dspState.eqEnabled,
                                    accentColor = accentColor,
                                    onGainChange = { onBandChange(idx, it) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = DividerSubtle)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Hardware Bass Boost", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                            Text("${dspState.bassBoostStrength / 10}%", color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = dspState.bassBoostStrength.toFloat(),
                            onValueChange = { onBassBoostChange(it.roundToInt()) },
                            valueRange = 0f..1000f,
                            colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                        )
                    }

                    AudioLabTab.STAGE -> {
                        // 1. Preamp Loudness Boost
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Preamp / Loudness Boost", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("Clean gain boost for quiet recordings", color = TextSecondary, fontSize = 11.5.sp)
                            }
                            Text(
                                text = String.format(Locale.US, "+%.1f dB", dspState.preampGainDb),
                                color = accentColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = dspState.preampGainDb,
                            onValueChange = onPreampChange,
                            valueRange = 0f..10f,
                            colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 2. 3D Soundstage Virtualizer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("3D Soundstage", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("Wider stereo image for headphones", color = TextSecondary, fontSize = 11.5.sp)
                            }
                            Text(
                                text = "${dspState.virtualizerStrength / 10}%",
                                color = accentColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = dspState.virtualizerStrength.toFloat(),
                            onValueChange = { onVirtualizerChange(it.roundToInt()) },
                            valueRange = 0f..1000f,
                            colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = DividerSubtle)
                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. True PCM Stereo Balance & Mono Mix
                        val balanceLabel = when {
                            abs(dspState.stereoBalance) < 0.02f -> "Center"
                            dspState.stereoBalance < 0f -> "L ${(abs(dspState.stereoBalance) * 100).roundToInt()}%"
                            else -> "R ${(dspState.stereoBalance * 100).roundToInt()}%"
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Stereo L / R Balance", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(balanceLabel, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = dspState.stereoBalance,
                            onValueChange = onBalanceChange,
                            valueRange = -1f..1f,
                            colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceElevated)
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Mono Audio Mix", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                                Text("Combine L & R channels for single-earbud listening", color = TextSecondary, fontSize = 11.5.sp)
                            }
                            Switch(
                                checked = dspState.monoMix,
                                onCheckedChange = onMonoMixChange,
                                colors = SwitchDefaults.colors(checkedTrackColor = accentColor, checkedThumbColor = CanvasBg)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = DividerSubtle)
                        Spacer(modifier = Modifier.height(12.dp))

                        // 4. Opt-In Room Reverb Row (User requested opt-in)
                        Text("Acoustic Room Reverb (Opt-In)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Hardware auxiliary ambience — Off by default", color = TextSecondary, fontSize = 11.5.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(ReverbPreset.entries) { preset ->
                                val selected = preset == dspState.reverbPreset
                                Surface(
                                    onClick = { onReverbChange(preset) },
                                    shape = CircleShape,
                                    color = if (selected) accentColor else SurfaceChip
                                ) {
                                    Text(
                                        text = preset.label,
                                        color = if (selected) CanvasBg else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }

                    AudioLabTab.SPEED -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Playback Tempo", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(String.format(Locale.US, "%.2fx", dspState.speed), color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = dspState.speed,
                            onValueChange = onSpeedChange,
                            valueRange = 0.50f..2.00f,
                            colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { spd ->
                                val active = abs(dspState.speed - spd) < 0.02f
                                Surface(
                                    onClick = { onSpeedChange(spd) },
                                    shape = CircleShape,
                                    color = if (active) accentColor else SurfaceChip
                                ) {
                                    Text(
                                        text = "${spd}x",
                                        color = if (active) CanvasBg else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Independent Pitch", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(String.format(Locale.US, "%.2fx", dspState.pitch), color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = dspState.pitch,
                            onValueChange = onPitchChange,
                            valueRange = 0.50f..2.00f,
                            colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = DividerSubtle)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Smart Silence Skip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceElevated)
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Smart Silence Skip", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("Automatically trim dead-air pauses during lectures or live sets", color = TextSecondary, fontSize = 11.5.sp)
                            }
                            Switch(
                                checked = dspState.skipSilence,
                                onCheckedChange = onSkipSilenceChange,
                                colors = SwitchDefaults.colors(checkedTrackColor = accentColor, checkedThumbColor = CanvasBg)
                            )
                        }
                    }

                    AudioLabTab.FLOW -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("A–B Section Looper", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
                            if (playback.currentTrack != null) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(onClick = { onNudgePosition(-10_000L) }, shape = CircleShape, color = SurfaceChip) {
                                        Icon(Icons.Rounded.Replay10, contentDescription = "-10s", tint = TextPrimary, modifier = Modifier.padding(6.dp).size(18.dp))
                                    }
                                    Surface(onClick = { onNudgePosition(10_000L) }, shape = CircleShape, color = SurfaceChip) {
                                        Icon(Icons.Rounded.Forward10, contentDescription = "+10s", tint = TextPrimary, modifier = Modifier.padding(6.dp).size(18.dp))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (playback.currentTrack == null) {
                                "Play a song to set A and B loop points."
                            } else {
                                "A: ${dspState.abLoop.pointAMs?.let { formatDuration(it) } ?: "--:--"}   •   B: ${dspState.abLoop.pointBMs?.let { formatDuration(it) } ?: "--:--"}"
                            },
                            color = TextSecondary,
                            fontSize = 12.5.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                onClick = onMarkA,
                                enabled = playback.currentTrack != null,
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceChip,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
                                    Text("Set A", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Surface(
                                onClick = onMarkB,
                                enabled = playback.currentTrack != null,
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceChip,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
                                    Text("Set B", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Surface(
                                onClick = { onToggleAbLoop(!dspState.abLoop.enabled) },
                                enabled = dspState.abLoop.isReady,
                                shape = RoundedCornerShape(10.dp),
                                color = if (dspState.abLoop.enabled) accentColor else SurfaceElevated,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
                                    Text(
                                        if (dspState.abLoop.enabled) "Active" else "Loop",
                                        color = if (dspState.abLoop.enabled) CanvasBg else TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Surface(
                                onClick = onClearAbLoop,
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceElevated
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)) {
                                    Text("Clear", color = TextSecondary, fontSize = 13.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        HorizontalDivider(color = DividerSubtle)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Bedtime, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Fade-Out Sleep Timer", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
                            }
                            if (dspState.sleepTimerRemainingMs > 0L) {
                                Text(formatDuration(dspState.sleepTimerRemainingMs), color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0 to "Off", 15 to "15m", 30 to "30m", 45 to "45m", 60 to "60m").forEach { (mins, label) ->
                                val isSelected = dspState.sleepTimerSelectedMinutes == mins
                                Surface(
                                    onClick = { onSetSleepMinutes(mins) },
                                    shape = CircleShape,
                                    color = if (isSelected) accentColor else SurfaceChip,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 9.dp)) {
                                        Text(
                                            text = label,
                                            color = if (isSelected) CanvasBg else TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        HorizontalDivider(color = DividerSubtle)
                        Spacer(modifier = Modifier.height(14.dp))

                        val fadeLabel = if (dspState.crossfadeSeconds == 0) "True Gapless" else "${dspState.crossfadeSeconds}s Auto-Fade"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Track Transitions", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
                            Text(fadeLabel, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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

    if (showSavePresetDialog) {
        var presetName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSavePresetDialog = false },
            containerColor = SurfaceSheet,
            title = { Text("Save EQ Preset", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = presetName,
                    onValueChange = { presetName = it },
                    singleLine = true,
                    placeholder = { Text("My Headphones", color = TextMuted) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (presetName.isNotBlank()) {
                            onSaveCustomPreset(presetName)
                            showSavePresetDialog = false
                        }
                    }
                ) {
                    Text("Save", color = accentColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSavePresetDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

/**
 * Smooth Cubic Bezier Frequency Response Curve Graph for the 5-Band EQ.
 */
@Composable
private fun EqFrequencyCurveGraph(
    bandGainsDb: List<Float>,
    enabled: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val lineColor = if (enabled) accentColor else TextMuted

    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        val w = size.width
        val h = size.height
        val midY = h / 2f

        // Dashed 0dB reference centerline
        drawLine(
            color = Color.White.copy(alpha = 0.14f),
            start = Offset(0f, midY),
            end = Offset(w, midY),
            strokeWidth = 1.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
        )

        val points = (0 until 5).map { idx ->
            val gain = bandGainsDb.getOrElse(idx) { 0f }.coerceIn(-12f, 12f)
            val x = (idx.toFloat() / 4f) * w
            val y = midY - (gain / 12f) * (h * 0.42f)
            Offset(x, y)
        }

        val curvePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.lastIndex) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
        }

        val fillPath = Path().apply {
            addPath(curvePath)
            lineTo(points.last().x, h)
            lineTo(points.first().x, h)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                listOf(lineColor.copy(alpha = if (enabled) 0.28f else 0.08f), Color.Transparent)
            )
        )
        drawPath(
            path = curvePath,
            color = lineColor,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        points.forEach { pt ->
            drawCircle(color = CanvasBg, radius = 6f, center = pt)
            drawCircle(color = lineColor, radius = 4f, center = pt)
        }
    }
}

/**
 * Custom Vertical Mixing-Console EQ Fader.
 * Supports smooth vertical drag, tap-to-set, and double-tap to reset cleanly to 0.0dB.
 */
@Composable
private fun VerticalEqFader(
    label: String,
    gainDb: Float,
    enabled: Boolean,
    accentColor: Color,
    onGainChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTrack = if (enabled) accentColor else TextMuted

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = String.format(Locale.US, "%+.0f", gainDb),
            color = if (enabled && abs(gainDb) > 0.2f) accentColor else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Canvas(
            modifier = Modifier
                .weight(1f)
                .width(42.dp)
                .pointerInput(label) {
                    detectTapGestures(
                        onDoubleTap = { onGainChange(0f) },
                        onTap = { offset ->
                            val frac = 1f - (offset.y / size.height.toFloat()).coerceIn(0f, 1f)
                            val db = ((frac * 24f) - 12f).coerceIn(-12f, 12f)
                            onGainChange((db * 2f).roundToInt() / 2f)
                        }
                    )
                }
                .pointerInput(label) {
                    detectVerticalDragGestures { change, _ ->
                        change.consume()
                        val frac = 1f - (change.position.y / size.height.toFloat()).coerceIn(0f, 1f)
                        val db = ((frac * 24f) - 12f).coerceIn(-12f, 12f)
                        onGainChange((db * 2f).roundToInt() / 2f)
                    }
                }
        ) {
            val centerX = size.width / 2f
            val padY = 10f
            val trackH = (size.height - padY * 2f).coerceAtLeast(10f)
            val zeroY = padY + trackH / 2f
            val norm = ((gainDb + 12f) / 24f).coerceIn(0f, 1f)
            val thumbY = padY + (1f - norm) * trackH

            // Background rail
            drawLine(
                color = Color.White.copy(alpha = 0.14f),
                start = Offset(centerX, padY),
                end = Offset(centerX, padY + trackH),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )

            // Center 0dB notch
            drawLine(
                color = Color.White.copy(alpha = 0.30f),
                start = Offset(centerX - 8f, zeroY),
                end = Offset(centerX + 8f, zeroY),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )

            // Active fill from 0dB to thumb
            drawLine(
                color = activeTrack,
                start = Offset(centerX, zeroY),
                end = Offset(centerX, thumbY),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )

            // Fader Cap
            drawCircle(color = TextPrimary, radius = 11f, center = Offset(centerX, thumbY))
            drawCircle(color = activeTrack, radius = 5f, center = Offset(centerX, thumbY))
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ============================================================================
// SUPPORTING SHEETS, DIALOGS & SHARED ARTWORK / VISUALIZER
// ============================================================================

@Composable
private fun SortPickerSheet(
    currentSort: LibrarySort,
    accentColor: Color,
    onDismiss: () -> Unit,
    onSelectSort: (LibrarySort) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceSheet,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text("Sort library by", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))
            LibrarySort.entries.forEach { sort ->
                val selected = sort == currentSort
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectSort(sort) }
                        .padding(vertical = 13.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sort.label,
                        color = if (selected) accentColor else TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    if (selected) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    }
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
        containerColor = SurfaceSheet,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text("Up Next (${queue.size})", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))
            if (queue.isEmpty()) {
                Text("Queue is empty.", color = TextSecondary, modifier = Modifier.padding(vertical = 24.dp))
            } else {
                LazyColumn(modifier = Modifier.height(340.dp)) {
                    itemsIndexed(queue, key = { idx, t -> "${t.id}_q_$idx" }) { index, track ->
                        val active = index == currentIndex
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) Color.White.copy(alpha = 0.06f) else Color.Transparent)
                                .clickable { onSelectIndex(index) }
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TrackArtwork(track.artworkPath, track.id, 42.dp, 6.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    color = if (active) accentColor else TextPrimary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(track.artist, color = TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            if (!active) {
                                IconButton(onClick = { onRemoveIndex(index) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Remove", tint = TextMuted, modifier = Modifier.size(16.dp))
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
    onBeamTrack: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onEditInfo: () -> Unit,
    onToggleLike: () -> Unit,
    onRemoveFromLibrary: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceSheet,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TrackArtwork(track.artworkPath, track.id, 44.dp, 6.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(track.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${track.artist} • ${track.qualityBadge}", color = TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DividerSubtle)
            Spacer(modifier = Modifier.height(6.dp))

            SheetActionItem("Play Next", Icons.Rounded.QueuePlayNext, TextPrimary, onPlayNext)
            SheetActionItem("Send via StudioBeam", Icons.Rounded.WifiTethering, SpotifyAccentFallback, onBeamTrack)
            SheetActionItem("Add to Playlist", Icons.AutoMirrored.Rounded.PlaylistAdd, TextPrimary, onAddToPlaylist)
            SheetActionItem("Edit Track Info", Icons.Rounded.Edit, TextPrimary, onEditInfo)
            SheetActionItem(
                if (isLiked) "Remove from Liked Songs" else "Add to Liked Songs",
                if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                if (isLiked) SpotifyAccentFallback else TextPrimary,
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
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(21.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(label, color = tint, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun EditTrackInfoDialog(
    track: AudioTrack,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(track.title) }
    var artist by remember { mutableStateOf(track.artist) }
    var album by remember { mutableStateOf(track.album) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceSheet,
        title = { Text("Edit Track Info", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, singleLine = true, label = { Text("Title") })
                OutlinedTextField(value = artist, onValueChange = { artist = it }, singleLine = true, label = { Text("Artist") })
                OutlinedTextField(value = album, onValueChange = { album = it }, singleLine = true, label = { Text("Album") })
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(title, artist, album) }) {
                Text("Save", color = TextPrimary, fontWeight = FontWeight.Bold)
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
private fun PlaylistPickerSheet(
    track: AudioTrack,
    playlists: List<UserPlaylist>,
    onDismiss: () -> Unit,
    onPickPlaylist: (String) -> Unit,
    onCreateNew: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceSheet,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text("Add \"${track.title}\" to...", fontWeight = FontWeight.Bold, fontSize = 16.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(10.dp))
            SheetActionItem("New Playlist", Icons.Rounded.Add, SpotifyAccentFallback, onCreateNew)
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
        containerColor = SurfaceSheet,
        title = { Text("New Playlist", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                placeholder = { Text("Late Night Mix", color = TextMuted) }
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name) }) {
                Text("Create", color = TextPrimary, fontWeight = FontWeight.Bold)
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
            .background(Brush.linearGradient(listOf(fallbackPalette.primary, fallbackPalette.secondary))),
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
                modifier = Modifier.size(if (size != null) size * 0.44f else 68.dp)
            )
        }
    }
}

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
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(maxHeight)
    ) {
        for (i in 0 until barCount) {
            val animFraction by infinite.animateFloat(
                initialValue = 0.25f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = ((350 + i * 105) * durationScale).roundToInt().coerceAtLeast(180),
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$i"
            )
            val heightFraction = if (isPlaying) animFraction else 0.25f
            Box(
                modifier = Modifier
                    .width(2.8.dp)
                    .height(maxHeight * heightFraction)
                    .clip(CircleShape)
                    .background(barColor)
            )
        }
    }
}
