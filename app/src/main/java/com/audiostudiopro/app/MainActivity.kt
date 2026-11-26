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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Loop
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
import androidx.compose.material.icons.rounded.Speed
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private val ObsidianBg = Color(0xFF08080A)
private val GlassSurfaceLow = Color.White.copy(alpha = 0.055f)
private val GlassSurfaceMid = Color.White.copy(alpha = 0.09f)
private val GlassBorder = Color.White.copy(alpha = 0.13f)
private val SpecularHighlight = Color.White.copy(alpha = 0.22f)
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

private enum class BeamSheetTab(val label: String) {
    SEND("Send Music"),
    RECEIVE("Receive Music")
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
    val sortOrder by vm.sortOrder.collectAsState()
    val filterMode by vm.filterMode.collectAsState()
    val playback by vm.playbackState.collectAsState()
    val ambient by vm.ambientPalette.collectAsState()
    val dspState by vm.dspState.collectAsState()
    val beamState by vm.beamState.collectAsState()
    val isImporting by vm.isImporting.collectAsState()
    val banner by vm.statusBanner.collectAsState()

    var selectedTab by remember { mutableStateOf(LibraryTab.LIBRARY) }
    var openPlaylistId by remember { mutableStateOf<String?>(null) }
    var showNowPlaying by remember { mutableStateOf(false) }
    var showAudioLabSheet by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var showBeamSheet by remember { mutableStateOf(false) }
    var beamInitialTab by remember { mutableStateOf(BeamSheetTab.SEND) }
    var beamPreselectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var trackForOptions by remember { mutableStateOf<AudioTrack?>(null) }
    var trackForMetadataEdit by remember { mutableStateOf<AudioTrack?>(null) }
    var trackForPlaylistPicker by remember { mutableStateOf<AudioTrack?>(null) }

    // Library multi-select mode (long-press any track to batch-beam or batch-queue)
    var multiSelectIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    val isMultiSelectActive = multiSelectIds.isNotEmpty()

    BackHandler(enabled = showNowPlaying) {
        showNowPlaying = false
    }
    BackHandler(enabled = !showNowPlaying && isMultiSelectActive) {
        multiSelectIds = emptySet()
    }
    BackHandler(enabled = !showNowPlaying && !isMultiSelectActive && openPlaylistId != null) {
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

    val filteredTracks = remember(tracks, searchQuery, sortOrder, filterMode) {
        val q = searchQuery.trim().lowercase(Locale.US)
        val searched = if (q.isEmpty()) tracks else tracks.filter {
            it.title.lowercase(Locale.US).contains(q) ||
                it.artist.lowercase(Locale.US).contains(q) ||
                it.album.lowercase(Locale.US).contains(q) ||
                it.codec.lowercase(Locale.US).contains(q)
        }
        val filtered = when (filterMode) {
            LibraryFilter.ALL -> searched
            LibraryFilter.LOSSLESS -> searched.filter { it.isLossless }
            LibraryFilter.RECENTLY_PLAYED -> searched.filter { it.lastPlayedAt > 0L }
        }
        when (sortOrder) {
            LibrarySort.RECENT -> filtered.sortedByDescending { it.dateAdded }
            LibrarySort.TITLE -> filtered.sortedBy { it.title.lowercase(Locale.US) }
            LibrarySort.ARTIST -> filtered.sortedBy { it.artist.lowercase(Locale.US) }
            LibrarySort.DURATION -> filtered.sortedByDescending { it.durationMs }
            LibrarySort.MOST_PLAYED -> filtered.sortedByDescending { it.playCount }
        }
    }

    val likedTracks = remember(tracks, likedIds, searchQuery) {
        val base = tracks.filter { likedIds.contains(it.id) }
        val q = searchQuery.trim().lowercase(Locale.US)
        if (q.isEmpty()) base
        else base.filter {
            it.title.lowercase(Locale.US).contains(q) ||
                it.artist.lowercase(Locale.US).contains(q) ||
                it.album.lowercase(Locale.US).contains(q)
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
        // Breathing Aurora Ambient Backdrop
        AmbientGlowCanvas(
            primary = animPrimary,
            secondary = animSecondary,
            intensity = if (playback.isPlaying) 0.36f else 0.19f,
            animateDrift = playback.isPlaying
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Studio Header (clean one-row studio bar with Beam, DSP, + Import)
            if (isMultiSelectActive) {
                MultiSelectHeader(
                    selectedCount = multiSelectIds.size,
                    totalCount = filteredTracks.size,
                    accentColor = animAccent,
                    onCancel = { multiSelectIds = emptySet() },
                    onSelectAll = {
                        multiSelectIds = if (multiSelectIds.size == filteredTracks.size) {
                            emptySet()
                        } else {
                            filteredTracks.map { it.id }.toSet()
                        }
                    }
                )
            } else {
                StudioHeader(
                    trackCount = tracks.size,
                    isImporting = isImporting,
                    dspActive = dspState.hasActiveProcessing,
                    beamActive = beamState.mode == BeamMode.SENDING || beamState.mode == BeamMode.RECEIVING,
                    accentColor = animAccent,
                    onBeamClick = {
                        beamInitialTab = if (tracks.isEmpty()) BeamSheetTab.RECEIVE else BeamSheetTab.SEND
                        beamPreselectedIds = emptySet()
                        showBeamSheet = true
                    },
                    onImportClick = { importLauncher.launch(arrayOf("audio/*")) },
                    onAudioLabClick = { showAudioLabSheet = true }
                )
            }

            // Segmented Apple-style Navigation Pill
            if (activePlaylist == null) {
                SegmentedGlassTabs(
                    selected = selectedTab,
                    onSelect = {
                        selectedTab = it
                        multiSelectIds = emptySet()
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )

                if (selectedTab != LibraryTab.PLAYLISTS) {
                    GlassSearchBar(
                        query = searchQuery,
                        onQueryChange = vm::setSearchQuery,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp)
                    )
                }

                if (selectedTab == LibraryTab.LIBRARY && tracks.isNotEmpty()) {
                    LibraryFilterSortStrip(
                        activeFilter = filterMode,
                        activeSort = sortOrder,
                        accentColor = animAccent,
                        onSelectFilter = vm::setFilterMode,
                        onCycleSort = {
                            val entries = LibrarySort.entries
                            val next = entries[(entries.indexOf(sortOrder) + 1) % entries.size]
                            vm.setSortOrder(next)
                        }
                    )
                }
            }

            // Main Content Area
            Box(modifier = Modifier.weight(1f)) {
                val bottomPad = when {
                    isMultiSelectActive -> 112.dp
                    playback.currentTrack != null -> 114.dp
                    else -> 28.dp
                }

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
                            bottomPadding = bottomPad,
                            onBack = { openPlaylistId = null },
                            onPlayAll = { vm.playTracks(playlistTracks, 0, shuffle = false) },
                            onShuffle = { vm.playTracks(playlistTracks, 0, shuffle = true) },
                            onBeamPlaylist = {
                                beamPreselectedIds = playlistTracks.map { it.id }.toSet()
                                beamInitialTab = BeamSheetTab.SEND
                                showBeamSheet = true
                            },
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
                            selectedIds = multiSelectIds,
                            currentTrackId = playback.currentTrack?.id,
                            isPlaying = playback.isPlaying,
                            accentColor = animAccent,
                            emptyTitle = if (filterMode == LibraryFilter.ALL) "Your Studio Library is Empty" else "No Matching Tracks",
                            emptySubtitle = if (filterMode == LibraryFilter.ALL) {
                                "Import FLAC, WAV, MP3, or M4A tracks from storage—or receive a full collection in seconds with StudioBeam."
                            } else {
                                "Try switching the filter chip above or clearing your search query."
                            },
                            showImportCta = searchQuery.isBlank() && filterMode == LibraryFilter.ALL,
                            bottomPadding = bottomPad,
                            onImportClick = { importLauncher.launch(arrayOf("audio/*")) },
                            onReceiveBeamClick = {
                                beamInitialTab = BeamSheetTab.RECEIVE
                                showBeamSheet = true
                            },
                            onPlayAll = { vm.playTracks(filteredTracks, 0, shuffle = false) },
                            onShuffle = { vm.playTracks(filteredTracks, 0, shuffle = true) },
                            onTrackClick = { idx, track ->
                                if (isMultiSelectActive) {
                                    multiSelectIds = if (multiSelectIds.contains(track.id)) {
                                        multiSelectIds - track.id
                                    } else {
                                        multiSelectIds + track.id
                                    }
                                } else {
                                    vm.playTracks(filteredTracks, idx, shuffle = false)
                                }
                            },
                            onTrackLongClick = { track ->
                                multiSelectIds = multiSelectIds + track.id
                            },
                            onToggleLike = vm::toggleLike,
                            onTrackOptions = { trackForOptions = it }
                        )
                    }

                    selectedTab == LibraryTab.LIKED -> {
                        TrackCollectionView(
                            tracks = likedTracks,
                            likedIds = likedIds,
                            selectedIds = multiSelectIds,
                            currentTrackId = playback.currentTrack?.id,
                            isPlaying = playback.isPlaying,
                            accentColor = animAccent,
                            emptyTitle = "No Favorite Tracks Yet",
                            emptySubtitle = "Tap the heart on any track to curate your personal high-rotation collection.",
                            showImportCta = false,
                            bottomPadding = bottomPad,
                            onImportClick = {},
                            onReceiveBeamClick = {},
                            onPlayAll = { vm.playTracks(likedTracks, 0, shuffle = false) },
                            onShuffle = { vm.playTracks(likedTracks, 0, shuffle = true) },
                            onTrackClick = { idx, track ->
                                if (isMultiSelectActive) {
                                    multiSelectIds = if (multiSelectIds.contains(track.id)) {
                                        multiSelectIds - track.id
                                    } else {
                                        multiSelectIds + track.id
                                    }
                                } else {
                                    vm.playTracks(likedTracks, idx, shuffle = false)
                                }
                            },
                            onTrackLongClick = { track ->
                                multiSelectIds = multiSelectIds + track.id
                            },
                            onToggleLike = vm::toggleLike,
                            onTrackOptions = { trackForOptions = it }
                        )
                    }

                    selectedTab == LibraryTab.PLAYLISTS -> {
                        PlaylistsGridList(
                            playlists = playlists,
                            allTracks = tracks,
                            accentColor = animAccent,
                            bottomPadding = bottomPad,
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
                .padding(top = 66.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF1A1A22).copy(alpha = 0.95f),
                border = BorderStroke(1.dp, SpecularHighlight),
                shadowElevation = 14.dp
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

        // Floating Multi-Select Batch Action Dock (Beam Selected / Queue Next)
        AnimatedVisibility(
            visible = isMultiSelectActive && !showNowPlaying,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFF16161E).copy(alpha = 0.95f),
                border = BorderStroke(1.dp, SpecularHighlight),
                shadowElevation = 20.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = {
                            beamPreselectedIds = multiSelectIds
                            beamInitialTab = BeamSheetTab.SEND
                            multiSelectIds = emptySet()
                            showBeamSheet = true
                        },
                        shape = CircleShape,
                        color = animAccent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 11.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.WifiTethering, contentDescription = null, tint = ObsidianBg, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Beam (${multiSelectIds.size})",
                                color = ObsidianBg,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Surface(
                        onClick = {
                            val selectedList = tracks.filter { multiSelectIds.contains(it.id) }
                            vm.queueBatchNext(selectedList)
                            multiSelectIds = emptySet()
                        },
                        shape = CircleShape,
                        color = GlassSurfaceMid,
                        border = BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 11.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.QueuePlayNext, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play Next", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Floating Glass Mini-Player (with Horizontal Swipe-to-Skip Gesture)
        AnimatedVisibility(
            visible = playback.currentTrack != null && !showNowPlaying && !isMultiSelectActive,
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
                    onSkipNext = vm::skipNext,
                    onSkipPrev = vm::skipPrevious
                )
            }
        }

        // Full-Screen Now Playing Sheet (Touch-Isolated, Swipe-Down to Collapse, Waveform Scrubber)
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
                    onOpenQueue = { showQueueSheet = true },
                    onBeamCurrent = {
                        beamPreselectedIds = setOf(current.id)
                        beamInitialTab = BeamSheetTab.SEND
                        showBeamSheet = true
                    }
                )
            }
        }
    }

    // StudioBeam (ASP1) High-Speed Local P2P Transfer Bottom Sheet
    if (showBeamSheet) {
        StudioBeamSheet(
            tracks = tracks,
            beamState = beamState,
            initialTab = beamInitialTab,
            preselectedTrackIds = beamPreselectedIds,
            accentColor = animAccent,
            onDismiss = {
                vm.stopBeamScanning()
                showBeamSheet = false
            },
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

    // Pro Audio Lab Bottom Sheet
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
            onNudgePosition = vm::nudgePosition,
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
            onBeamTrack = {
                beamPreselectedIds = setOf(track.id)
                beamInitialTab = BeamSheetTab.SEND
                trackForOptions = null
                showBeamSheet = true
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

@Composable
private fun AmbientGlowCanvas(
    primary: Color,
    secondary: Color,
    intensity: Float,
    animateDrift: Boolean = false
) {
    val infinite = rememberInfiniteTransition(label = "auroraDrift")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auroraPhase"
    )

    val driftX = if (animateDrift) cos(phase) * 0.08f else 0f
    val driftY = if (animateDrift) sin(phase) * 0.06f else 0f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val c1 = Offset(size.width * (0.20f + driftX), size.height * (0.14f + driftY))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primary.copy(alpha = intensity), Color.Transparent),
                center = c1,
                radius = size.width * 0.98f
            ),
            radius = size.width * 0.98f,
            center = c1
        )
        val c2 = Offset(size.width * (0.82f - driftX), size.height * (0.72f - driftY))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(secondary.copy(alpha = intensity * 0.92f), Color.Transparent),
                center = c2,
                radius = size.width * 0.92f
            ),
            radius = size.width * 0.92f,
            center = c2
        )
    }
}

@Composable
private fun StudioHeader(
    trackCount: Int,
    isImporting: Boolean,
    dspActive: Boolean,
    beamActive: Boolean,
    accentColor: Color,
    onBeamClick: () -> Unit,
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
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.6).sp
            )
            Text(
                text = if (trackCount > 0) "PRO ENGINE • $trackCount TRACKS" else "PRO LOCAL ENGINE",
                color = TextTertiary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.4.sp
            )
        }

        // StudioBeam P2P Transfer Pill
        Surface(
            onClick = onBeamClick,
            shape = CircleShape,
            color = if (beamActive) accentColor.copy(alpha = 0.25f) else GlassSurfaceLow,
            border = BorderStroke(
                1.dp,
                if (beamActive) accentColor.copy(alpha = 0.65f) else GlassBorder
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WifiTethering,
                    contentDescription = "StudioBeam Transfer",
                    tint = if (beamActive) accentColor else TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Beam",
                    color = if (beamActive) accentColor else TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.width(7.dp))

        // DSP Lab Quick Icon Pill
        Surface(
            onClick = onAudioLabClick,
            shape = CircleShape,
            color = if (dspActive) accentColor.copy(alpha = 0.24f) else GlassSurfaceLow,
            border = BorderStroke(
                1.dp,
                if (dspActive) accentColor.copy(alpha = 0.58f) else GlassBorder
            )
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = "Audio Lab",
                    tint = if (dspActive) accentColor else TextPrimary,
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(7.dp))

        // Prominent + Import Pill
        Surface(
            onClick = onImportClick,
            enabled = !isImporting,
            shape = CircleShape,
            color = TextPrimary,
            contentColor = ObsidianBg
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                    text = if (isImporting) "Wait" else "Import",
                    color = ObsidianBg,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MultiSelectHeader(
    selectedCount: Int,
    totalCount: Int,
    accentColor: Color,
    onCancel: () -> Unit,
    onSelectAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCancel) {
            Icon(Icons.Rounded.Close, contentDescription = "Cancel selection", tint = TextPrimary)
        }
        Text(
            text = "$selectedCount Selected",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Surface(
            onClick = onSelectAll,
            shape = CircleShape,
            color = accentColor.copy(alpha = 0.18f),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(Icons.Rounded.DoneAll, contentDescription = null, tint = accentColor, modifier = Modifier.size(15.dp))
                Text(
                    text = if (selectedCount == totalCount) "Clear" else "Select All",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun LibraryFilterSortStrip(
    activeFilter: LibraryFilter,
    activeSort: LibrarySort,
    accentColor: Color,
    onSelectFilter: (LibraryFilter) -> Unit,
    onCycleSort: () -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(LibraryFilter.entries) { filter ->
            val selected = filter == activeFilter
            Surface(
                onClick = { onSelectFilter(filter) },
                shape = CircleShape,
                color = if (selected) accentColor.copy(alpha = 0.22f) else GlassSurfaceLow,
                border = BorderStroke(1.dp, if (selected) accentColor.copy(alpha = 0.55f) else GlassBorder)
            ) {
                Text(
                    text = filter.label,
                    color = if (selected) TextPrimary else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
        item {
            Surface(
                onClick = onCycleSort,
                shape = CircleShape,
                color = GlassSurfaceMid,
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Rounded.SwapVert, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                    Text(
                        text = activeSort.label,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
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
                    .background(if (active) Color.White.copy(alpha = 0.15f) else Color.Transparent)
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
            .padding(horizontal = 14.dp, vertical = 10.dp),
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
                    text = "Search songs, artists, albums, or FLAC…",
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
    selectedIds: Set<String>,
    currentTrackId: String?,
    isPlaying: Boolean,
    accentColor: Color,
    emptyTitle: String,
    emptySubtitle: String,
    showImportCta: Boolean,
    bottomPadding: Dp,
    onImportClick: () -> Unit,
    onReceiveBeamClick: () -> Unit,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onTrackClick: (Int, AudioTrack) -> Unit,
    onTrackLongClick: (AudioTrack) -> Unit,
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
                    .size(74.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(GlassSurfaceMid)
                    .border(1.dp, SpecularHighlight, RoundedCornerShape(24.dp)),
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
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        onClick = onImportClick,
                        shape = CircleShape,
                        color = TextPrimary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null, tint = ObsidianBg, modifier = Modifier.size(18.dp))
                            Text("Import Files", color = ObsidianBg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                    Surface(
                        onClick = onReceiveBeamClick,
                        shape = CircleShape,
                        color = accentColor.copy(alpha = 0.20f),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.WifiTethering, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                            Text("Receive Beam", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        return
    }

    val multiSelectMode = selectedIds.isNotEmpty()

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
        if (!multiSelectMode) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
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
        }

        itemsIndexed(tracks, key = { _, t -> t.id }) { index, track ->
            val isCurrent = track.id == currentTrackId
            val isSelected = selectedIds.contains(track.id)
            TrackRowCard(
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
        shape = RoundedCornerShape(15.dp),
        color = if (emphasized) accentColor.copy(alpha = 0.22f) else GlassSurfaceLow,
        border = BorderStroke(
            1.dp,
            if (emphasized) accentColor.copy(alpha = 0.50f) else GlassBorder
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
    multiSelectMode: Boolean,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onToggleLike: () -> Unit,
    onMoreClick: () -> Unit
) {
    val highlight = isSelected || isCurrent
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (highlight) accentColor.copy(alpha = 0.14f) else GlassSurfaceLow)
            .border(
                width = 1.dp,
                color = if (highlight) accentColor.copy(alpha = 0.45f) else GlassBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (multiSelectMode) {
            Icon(
                imageVector = if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) accentColor else TextTertiary,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(22.dp)
            )
        }

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
                        .background(Color.Black.copy(alpha = 0.48f)),
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.title,
                    color = if (isCurrent) accentColor else TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (track.isLossless) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.20f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = track.codec,
                            color = accentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${track.artist} • ${formatDuration(track.durationMs)}",
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (!multiSelectMode) {
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
                        text = "Create custom mixes and beam entire playlists to friends.",
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
    onSkipNext: () -> Unit,
    onSkipPrev: () -> Unit
) {
    var accumulatedDrag by remember { mutableFloatStateOf(0f) }

    Surface(
        onClick = onExpand,
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF14141B).copy(alpha = 0.90f),
        border = BorderStroke(1.dp, SpecularHighlight),
        shadowElevation = 22.dp,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(track.id) {
                detectHorizontalDragGestures(
                    onDragStart = { accumulatedDrag = 0f },
                    onHorizontalDrag = { _, dragAmount -> accumulatedDrag += dragAmount },
                    onDragEnd = {
                        when {
                            accumulatedDrag < -90f -> onSkipNext()
                            accumulatedDrag > 90f -> onSkipPrev()
                        }
                        accumulatedDrag = 0f
                    },
                    onDragCancel = { accumulatedDrag = 0f }
                )
            }
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
                    size = 46.dp,
                    cornerRadius = 11.dp
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
                        modifier = Modifier.size(27.dp)
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
                        .background(
                            Brush.horizontalGradient(listOf(accentColor.copy(alpha = 0.7f), accentColor))
                        )
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
    onOpenQueue: () -> Unit,
    onBeamCurrent: () -> Unit
) {
    // Spring-physics artwork scaling: 0.86f paused -> 1.0f playing
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
    var pullDownOffsetPx by remember { mutableFloatStateOf(0f) }
    var artworkSwipeDrag by remember { mutableFloatStateOf(0f) }

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

    val loopStartFrac = if (dspState.abLoop.enabled && dspState.abLoop.trackId == track.id) {
        dspState.abLoop.pointAMs?.let { (it.toFloat() / duration.toFloat()).coerceIn(0f, 1f) }
    } else null
    val loopEndFrac = if (dspState.abLoop.enabled && dspState.abLoop.trackId == track.id) {
        dspState.abLoop.pointBMs?.let { (it.toFloat() / duration.toFloat()).coerceIn(0f, 1f) }
    } else null

    val touchBarrier = remember { MutableInteractionSource() }

    // FIX: Root Box explicitly consumes all taps (clickable with no ripple) so ZERO touches ever
    // pass through to the StudioHeader (+ Import) buttons underneath! Also supports swipe-down dismiss.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationY = pullDownOffsetPx.coerceAtLeast(0f)
            }
            .background(ObsidianBg)
            .clickable(
                interactionSource = touchBarrier,
                indication = null,
                onClick = {}
            )
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        pullDownOffsetPx = (pullDownOffsetPx + dragAmount).coerceIn(0f, 420f)
                    },
                    onDragEnd = {
                        if (pullDownOffsetPx > 140f) {
                            onCollapse()
                        }
                        pullDownOffsetPx = 0f
                    },
                    onDragCancel = { pullDownOffsetPx = 0f }
                )
            }
    ) {
        AmbientGlowCanvas(
            primary = primaryColor,
            secondary = secondaryColor,
            intensity = if (playback.isPlaying) 0.54f else 0.30f,
            animateDrift = playback.isPlaying
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Subtle iOS Grab Handle
            Box(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 4.dp)
                    .width(38.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.24f))
            )

            // Top Bar (Top-right Acoustic Wave Pill now opens Audio Lab cleanly!)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 10.dp),
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

                // Purposeful Interactive Wave Pill -> Opens Pro DSP Audio Lab
                Surface(
                    onClick = onOpenAudioLab,
                    shape = CircleShape,
                    color = if (dspState.hasActiveProcessing) accentColor.copy(alpha = 0.22f) else GlassSurfaceMid,
                    border = BorderStroke(
                        1.dp,
                        if (dspState.hasActiveProcessing) accentColor.copy(alpha = 0.6f) else GlassBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        AcousticPulseBars(
                            isPlaying = playback.isPlaying,
                            barColor = accentColor,
                            barCount = 4,
                            maxHeight = 16.dp,
                            speedMultiplier = dspState.speed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.45f))

            // Hero Artwork with Spring Physics & Horizontal Swipe-to-Skip
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .aspectRatio(1f)
                    .graphicsLayer {
                        scaleX = artworkScale
                        scaleY = artworkScale
                    }
                    .shadow(
                        elevation = if (playback.isPlaying) 36.dp else 12.dp,
                        shape = RoundedCornerShape(26.dp),
                        ambientColor = primaryColor,
                        spotColor = primaryColor
                    )
                    .pointerInput(track.id) {
                        detectHorizontalDragGestures(
                            onDragStart = { artworkSwipeDrag = 0f },
                            onHorizontalDrag = { _, dragAmount -> artworkSwipeDrag += dragAmount },
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
                    cornerRadius = 26.dp,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Audiophile Codec & Bitrate Telemetry Badge
            Surface(
                shape = CircleShape,
                color = GlassSurfaceLow,
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.HighQuality,
                        contentDescription = null,
                        tint = if (track.isLossless) accentColor else TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = track.qualityBadge,
                        color = if (track.isLossless) accentColor else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.55f))

            // Track Metadata, Beam Quick Share & Favorite
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
                IconButton(onClick = onBeamCurrent) {
                    Icon(
                        imageVector = Icons.Rounded.WifiTethering,
                        contentDescription = "Beam this track",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
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

            // Flagship Custom Interactive Acoustic Waveform Scrubber
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
                    .height(44.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

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

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Apple HIG Transport Row
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
                    shadowElevation = 12.dp,
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

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Glass Utility Dock (Audio Lab & Up Next Queue)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
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

/**
 * Custom Interactive Acoustic Waveform Scrubber with tap-to-seek, drag-to-scrub,
 * and visual A-B loop range highlights.
 */
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
                    onDragEnd = {
                        onScrubFinished(latestDragFraction)
                    },
                    onDragCancel = {
                        onScrubFinished(latestDragFraction)
                    }
                )
            }
    ) {
        val totalW = size.width
        val totalH = size.height
        val slotW = totalW / barCount
        val barW = (slotW * 0.54f).coerceAtLeast(2.5f)
        val clampedProgress = progress.coerceIn(0f, 1f)

        // Optional A-B loop highlight bed
        if (loopStart != null && loopEnd != null && loopEnd > loopStart) {
            val startX = totalW * loopStart
            val endX = totalW * loopEnd
            drawRoundRect(
                color = accentColor.copy(alpha = 0.16f),
                topLeft = Offset(startX, 2f),
                size = Size((endX - startX).coerceAtLeast(4f), totalH - 4f),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }

        barAmplitudes.forEachIndexed { idx, amp ->
            val barFrac = idx.toFloat() / (barCount - 1)
            val barH = (totalH * 0.86f * amp).coerceAtLeast(6f)
            val x = idx * slotW + (slotW - barW) / 2f
            val y = (totalH - barH) / 2f

            val inLoop = loopStart != null && loopEnd != null && barFrac in loopStart..loopEnd
            val color = when {
                barFrac <= clampedProgress -> if (inLoop) accentColor else activeColor
                inLoop -> accentColor.copy(alpha = 0.48f)
                else -> Color.White.copy(alpha = 0.20f)
            }

            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barW, barH),
                cornerRadius = CornerRadius(barW / 2f, barW / 2f)
            )
        }

        // Playhead Glow Line
        val playX = (totalW * clampedProgress).coerceIn(2f, totalW - 2f)
        drawRoundRect(
            color = accentColor,
            topLeft = Offset(playX - 1.5f, 0f),
            size = Size(3f, totalH),
            cornerRadius = CornerRadius(3f, 3f)
        )
    }
}

@Composable
private fun StudioBeamSheet(
    tracks: List<AudioTrack>,
    beamState: StudioBeamUiState,
    initialTab: BeamSheetTab,
    preselectedTrackIds: Set<String>,
    accentColor: Color,
    onDismiss: () -> Unit,
    onSetRequirePin: (Boolean) -> Unit,
    onStartSending: (Set<String>) -> Unit,
    onStopSession: () -> Unit,
    onStartScanning: () -> Unit,
    onStopScanning: () -> Unit,
    onConnectPeer: (DiscoveredBeamPeer, String) -> Unit,
    onConnectManual: (String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var activeTab by remember { mutableStateOf(initialTab) }
    var selectedToSend by remember(preselectedTrackIds) { mutableStateOf(preselectedTrackIds) }
    var manualCode by remember { mutableStateOf("") }
    var manualPin by remember { mutableStateOf("") }
    var peerAwaitingPin by remember { mutableStateOf<DiscoveredBeamPeer?>(null) }

    LaunchedEffect(activeTab, beamState.mode) {
        if (activeTab == BeamSheetTab.RECEIVE && beamState.mode == BeamMode.IDLE) {
            onStartScanning()
        } else if (activeTab == BeamSheetTab.SEND) {
            onStopScanning()
        }
    }

    DisposableEffect(Unit) {
        onDispose { onStopScanning() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF101016),
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.WifiTethering, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("StudioBeam (ASP-v1)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        "Lossless multi-song Wi-Fi / Hotspot transfer • Zero internet data",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Send / Receive Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(GlassSurfaceLow)
                    .border(1.dp, GlassBorder, CircleShape)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BeamSheetTab.entries.forEach { tab ->
                    val selected = tab == activeTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .background(if (selected) accentColor.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable {
                                if (beamState.mode == BeamMode.IDLE || beamState.mode == BeamMode.COMPLETED || beamState.mode == BeamMode.ERROR) {
                                    activeTab = tab
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.label,
                            color = if (selected) TextPrimary else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Transfer / Broadcast / Completed / Error Banner Card
            if (beamState.mode != BeamMode.IDLE) {
                BeamActiveStatusCard(
                    beamState = beamState,
                    accentColor = accentColor,
                    onStopOrReset = onStopSession
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            when (activeTab) {
                BeamSheetTab.SEND -> {
                    if (beamState.mode != BeamMode.SENDING) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Select Tracks (${selectedToSend.size}/${tracks.size})",
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
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Optional 4-Digit PIN Security Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(GlassSurfaceLow)
                                .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Lock, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Require 4-Digit Beam PIN", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("Recommended on shared/public Wi-Fi", color = TextTertiary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = beamState.requirePin,
                                onCheckedChange = onSetRequirePin,
                                colors = SwitchDefaults.colors(checkedTrackColor = accentColor)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (tracks.isEmpty()) {
                            Text(
                                text = "Import tracks first before sending.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 20.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(210.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(tracks, key = { it.id }) { track ->
                                    val checked = selectedToSend.contains(track.id)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (checked) accentColor.copy(alpha = 0.16f) else GlassSurfaceLow)
                                            .border(1.dp, if (checked) accentColor.copy(alpha = 0.45f) else GlassBorder, RoundedCornerShape(12.dp))
                                            .clickable {
                                                selectedToSend = if (checked) selectedToSend - track.id else selectedToSend + track.id
                                            }
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (checked) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (checked) accentColor else TextTertiary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(track.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text("${track.artist} • ${track.codec}", color = TextSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            onClick = { onStartSending(selectedToSend) },
                            enabled = selectedToSend.isNotEmpty(),
                            shape = CircleShape,
                            color = if (selectedToSend.isNotEmpty()) accentColor else GlassSurfaceMid,
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
                                    tint = if (selectedToSend.isNotEmpty()) ObsidianBg else TextTertiary,
                                    modifier = Modifier.size(19.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Start StudioBeam (${selectedToSend.size} track${if (selectedToSend.size == 1) "" else "s"})",
                                    color = if (selectedToSend.isNotEmpty()) ObsidianBg else TextTertiary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                BeamSheetTab.RECEIVE -> {
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
                                    text = if (beamState.isScanning) "Scanning Nearby Wi-Fi / Hotspot…" else "Nearby StudioBeam Senders",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            if (beamState.isScanning) {
                                CircularProgressIndicator(
                                    color = accentColor,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (beamState.discoveredPeers.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GlassSurfaceLow)
                                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                                    .padding(18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "No senders detected yet",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Connect both phones to the same Wi-Fi or Mobile Hotspot and tap Start StudioBeam on the sender.",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                beamState.discoveredPeers.forEach { peer ->
                                    Surface(
                                        onClick = {
                                            if (peer.requiresPin) {
                                                peerAwaitingPin = peer
                                            } else {
                                                onConnectPeer(peer, "")
                                            }
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        color = accentColor.copy(alpha = 0.16f),
                                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
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
                                                    "${peer.trackCount} tracks • ${formatBytes(peer.totalBytes)}${if (peer.requiresPin) " • PIN" else ""}",
                                                    color = TextSecondary,
                                                    fontSize = 12.sp
                                                )
                                            }
                                            Text("Tap to Pull", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = GlassBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Manual Beam Code / Direct IP Fallback (for isolated college/office Wi-Fi)
                        Text("Direct Beam Code or IP (Isolated Wi-Fi Fallback)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = manualCode,
                                onValueChange = { manualCode = it },
                                singleLine = true,
                                placeholder = { Text("XXXXX-XXXXX or 192.168.x.x", color = TextTertiary, fontSize = 12.sp) },
                                modifier = Modifier.weight(1.7f)
                            )
                            OutlinedTextField(
                                value = manualPin,
                                onValueChange = { if (it.length <= 4) manualPin = it },
                                singleLine = true,
                                placeholder = { Text("PIN", color = TextTertiary, fontSize = 12.sp) },
                                modifier = Modifier.weight(0.8f)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            onClick = { onConnectManual(manualCode, manualPin) },
                            enabled = manualCode.isNotBlank(),
                            shape = CircleShape,
                            color = if (manualCode.isNotBlank()) TextPrimary else GlassSurfaceMid,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 12.dp)
                            ) {
                                Text(
                                    "Connect & Import Tracks",
                                    color = if (manualCode.isNotBlank()) ObsidianBg else TextTertiary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // PIN Prompt Dialog for protected discovered peer
    peerAwaitingPin?.let { peer ->
        var enteredPin by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { peerAwaitingPin = null },
            containerColor = Color(0xFF18181F),
            title = { Text("Enter 4-Digit Beam PIN", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Connecting to ${peer.deviceName}", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = { if (it.length <= 4) enteredPin = it },
                        singleLine = true,
                        placeholder = { Text("4-digit PIN shown on sender") }
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
            .clip(RoundedCornerShape(18.dp))
            .background(GlassSurfaceMid)
            .border(1.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (beamState.isBroadcasting) {
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = accentColor.copy(alpha = (1f - pulse) * 0.65f),
                        radius = size.minDimension * 0.5f * pulse,
                        style = Stroke(width = 3f)
                    )
                }
                Icon(Icons.Rounded.WifiTethering, contentDescription = null, tint = accentColor, modifier = Modifier.size(28.dp))
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
            Text(
                text = beamState.currentTrackTitle,
                color = accentColor,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (beamState.mode == BeamMode.SENDING && beamState.beamCode.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("BEAM CODE", color = TextTertiary, fontSize = 10.sp, letterSpacing = 1.4.sp, fontWeight = FontWeight.Bold)
            Text(
                text = beamState.beamCode,
                color = TextPrimary,
                fontSize = 22.sp,
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
                    .height(6.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${beamState.completedTracks} / ${beamState.totalTracks} tracks",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = if (beamState.speedMbps > 0.05f) {
                        String.format(Locale.US, "%.1f MB/s • %s", beamState.speedMbps, formatBytes(beamState.bytesTransferred))
                    } else {
                        formatBytes(beamState.bytesTransferred)
                    },
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onStopOrReset) {
            Text(
                text = if (beamState.mode == BeamMode.COMPLETED || beamState.mode == BeamMode.ERROR) "Done" else "Stop StudioBeam",
                color = Color(0xFFFF6B6B),
                fontWeight = FontWeight.SemiBold
            )
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
    onNudgePosition: (Long) -> Unit,
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
                    // A-B Section Looper + Precision ±10s Seek Nudges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("A-B Section Looper", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        if (playback.currentTrack != null) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    onClick = { onNudgePosition(-10_000L) },
                                    shape = CircleShape,
                                    color = GlassSurfaceLow,
                                    border = BorderStroke(1.dp, GlassBorder)
                                ) {
                                    Icon(Icons.Rounded.Replay10, contentDescription = "Back 10s", tint = TextPrimary, modifier = Modifier.padding(6.dp).size(18.dp))
                                }
                                Surface(
                                    onClick = { onNudgePosition(10_000L) },
                                    shape = CircleShape,
                                    color = GlassSurfaceLow,
                                    border = BorderStroke(1.dp, GlassBorder)
                                ) {
                                    Icon(Icons.Rounded.Forward10, contentDescription = "Forward 10s", tint = TextPrimary, modifier = Modifier.padding(6.dp).size(18.dp))
                                }
                            }
                        }
                    }
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
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
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
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
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
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
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
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)) {
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
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 9.dp)) {
                                    Text(label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = GlassBorder)
                    Spacer(modifier = Modifier.height(14.dp))

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
    onBeamTrack: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onEditInfo: () -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(track.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${track.artist} • ${track.qualityBadge}", color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))

            SheetActionItem("Play Next", Icons.Rounded.QueuePlayNext, TextPrimary, onPlayNext)
            SheetActionItem("Send via StudioBeam", Icons.Rounded.WifiTethering, Color(0xFF818CF8), onBeamTrack)
            SheetActionItem("Add to Playlist", Icons.AutoMirrored.Rounded.PlaylistAdd, TextPrimary, onAddToPlaylist)
            SheetActionItem("Edit Track Info", Icons.Rounded.Edit, TextPrimary, onEditInfo)
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
            .padding(vertical = 12.dp, horizontal = 8.dp),
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
        containerColor = Color(0xFF18181F),
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
            .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(cornerRadius)),
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
                tint = Color.White.copy(alpha = 0.88f),
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
