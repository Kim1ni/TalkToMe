package com.kmp.talktome.ui.features.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kmp.talktome.domain.model.SessionStatus
import com.kmp.talktome.ui.features.session.composables.AudioVisualizer
import com.kmp.talktome.ui.features.session.composables.EndSessionDialog
import com.kmp.talktome.ui.features.session.composables.ErrorOverlay
import com.kmp.talktome.ui.features.session.composables.SessionControls
import com.kmp.talktome.ui.features.session.composables.SessionHeader
import com.kmp.talktome.ui.features.session.composables.TranscriptOverlay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SessionScreen(
    viewModel: SessionViewModel = koinViewModel<SessionViewModel>(),
    onSessionComplete: () -> Unit, // Navigate to analysis with session ID
    onCancel: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if (state.status == SessionStatus.IDLE) {
            viewModel.startSession()
        }
    }

    LaunchedEffect(state.status) {
        if (state.status == SessionStatus.COMPLETED) {
            /*state.savedSessionId?.let { sessionId ->
                onSessionComplete(sessionId)
            }*/
            onSessionComplete()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SessionContent(
            state = state,
            onEndSession = { viewModel.endSession() },
            onRetry = { viewModel.retryConnection() },
            onCancel = onCancel
        )

        if (state.showEndConfirmation) {
            EndSessionDialog(
                onConfirm = { viewModel.confirmEndSession() },
                onDismiss = { viewModel.cancelEndSession() }
            )
        }
    }
}

@Composable
private fun SessionContent(
    state: SessionState,
    onEndSession: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECE5DD))
    ) {
        // Header
        SessionHeader(
            status = state.status,
            duration = state.duration,
            errorMessage = state.errorMessage
        )

        // Main content area
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            // Error overlay
            if (state.status == SessionStatus.ERROR) {
                ErrorOverlay(
                    errorMessage = state.errorMessage ?: "Connection error",
                    onRetry = onRetry,
                    onCancel = onCancel
                )
            }

            // Audio visualizer
            AudioVisualizer(
                status = state.status,
                volume = state.volume
            )

            // Transcript overlay
            TranscriptOverlay(
                transcript = state.transcript,
                partialTranscript = state.partialTranscript,
                status = state.status
            )
        }

        // Controls
        if (state.status != SessionStatus.ERROR) {
            SessionControls(
                status = state.status,
                onEndSession = onEndSession
            )
        }
    }
}

// --- FIX: Implement KMP-safe string formatting ---
fun formatDuration(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        // Format as HH:MM when over an hour
        val hoursString = hours.toString().padStart(2, '0')
        val minutesString = minutes.toString().padStart(2, '0')
        "$hoursString:$minutesString"
    } else {
        // Format as MM:SS when under an hour
        val minutesString = minutes.toString().padStart(2, '0')
        val secondsString = seconds.toString().padStart(2, '0')
        "$minutesString:$secondsString"
    }
}



