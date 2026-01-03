package com.kmp.talktome.ui.features.session_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.TtsVoice
import com.kmp.talktome.ui.features.session_details.composables.AudioPlayerBar
import com.kmp.talktome.ui.features.session_details.composables.FocusAreasCard
import com.kmp.talktome.ui.features.session_details.composables.PlaybackSettingsPopover
import com.kmp.talktome.ui.features.session_details.composables.SessionDetailsHeader
import com.kmp.talktome.ui.features.session_details.composables.SummaryCard
import com.kmp.talktome.ui.features.session_details.composables.TranscriptDivider
import com.kmp.talktome.ui.features.session_details.composables.TranscriptMessage
import com.kmp.talktome.ui.features.session_details.models.PlaybackMode
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SessionDetailsScreen(
    sessionId: String,
    viewModel: SessionDetailsViewModel = koinViewModel(),
    onBackPressed: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    //val context = LocalContext.current

    LaunchedEffect(sessionId) {
        viewModel.loadSession(sessionId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanup()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.session != null) {
            SessionDetailsContent(
                state = state,
                onBackPressed = {
                    viewModel.cleanup()
                    onBackPressed()
                },
                onTogglePlay = { viewModel.togglePlayback() },
                onSeek = { viewModel.seekTo(it) },
                onModeSwitch = { viewModel.switchPlaybackMode(it) },
                onVoiceSelected = { viewModel.selectVoice(it) },
                onToggleSettings = { viewModel.toggleSettings() },
                onDownloadAudio = { viewModel.downloadAudio(/*context*/) },
                onExportPdf = { viewModel.exportToPdf(/*context*/) }
            )
        }
    }
}

@Composable
private fun SessionDetailsContent(
    state: SessionDetailsState,
    onBackPressed: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    onModeSwitch: (PlaybackMode) -> Unit,
    onVoiceSelected: (TtsVoice) -> Unit,
    onToggleSettings: () -> Unit,
    onDownloadAudio: () -> Unit,
    onExportPdf: () -> Unit
) {
    val session = state.session ?: return
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to active message
    LaunchedEffect(state.activeMessageIndex, state.isPlaying) {
        if (state.isPlaying && state.activeMessageIndex >= 0) {
            coroutineScope.launch {
                listState.animateScrollToItem(
                    index = state.activeMessageIndex + 2, // Offset for header items
                    scrollOffset = -200
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFECE5DD))
        ) {
            // Header
            SessionDetailsHeader(
                session = session,
                onBackPressed = onBackPressed,
                onDownloadAudio = onDownloadAudio,
                onExportPdf = onExportPdf,
                isDownloading = state.isDownloading,
                hasAudio = state.audioUrl != null
            )

            // Content
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 200.dp)
            ) {
                // Summary Card
                if (session.analysis != null) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SummaryCard(summary = session.analysis.summary)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Transcript Section Header
                item {
                    TranscriptDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Transcript Messages
                itemsIndexed(session.transcript) { index, message ->
                    val isActive = state.isPlaying && index == state.activeMessageIndex

                    TranscriptMessage(
                        message = message,
                        isActive = isActive
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Focus Areas
                if (session.analysis?.areasOfImprovement?.isNotEmpty() == true) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        FocusAreasCard(areas = session.analysis.areasOfImprovement)
                    }
                }
            }
        }

        // Audio Player Bar
        AudioPlayerBar(
            state = state,
            onTogglePlay = onTogglePlay,
            onSeek = onSeek,
            onToggleSettings = onToggleSettings,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )

        // Settings Popover
        if (state.showSettings) {
            PlaybackSettingsPopover(
                playbackMode = state.playbackMode,
                voices = state.availableVoices,
                selectedVoice = state.selectedVoice,
                audioError = state.audioError,
                onModeSwitch = onModeSwitch,
                onVoiceSelected = onVoiceSelected,
                onDismiss = onToggleSettings,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 120.dp, end = 16.dp)
            )
        }
    }
}





