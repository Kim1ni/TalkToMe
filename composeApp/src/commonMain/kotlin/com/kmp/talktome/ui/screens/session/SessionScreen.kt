package com.kmp.talktome.ui.screens.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.SessionStatus
import com.kmp.talktome.ui.screens.session.composables.AudioVisualizer
import com.kmp.talktome.ui.screens.session.composables.EndSessionDialog
import com.kmp.talktome.ui.screens.session.composables.ErrorOverlay
import com.kmp.talktome.ui.screens.session.composables.SessionControls
import com.kmp.talktome.ui.screens.session.composables.SessionHeader
import com.kmp.talktome.ui.screens.session.composables.TranscriptOverlay
import com.kmp.talktome.ui.theme.TalkToMeTheme
import dev.icerock.moko.permissions.PermissionState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.session_error_title
import talktome.composeapp.generated.resources.session_processing_body
import talktome.composeapp.generated.resources.session_processing_title

@Composable
fun SessionScreen(
    viewModel: SessionViewModel = koinViewModel<SessionViewModel>(),
    onSessionComplete: () -> Unit,
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
            onSessionComplete()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (state.status == SessionStatus.ACTIVE || state.status == SessionStatus.CONNECTING) {
                viewModel.abandonSession() // A new method for "The user left abruptly"
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.status == SessionStatus.ANALYZING) {
            AnalysisScreen()
        } else {
            SessionContent(
                state = state,
                onEndSession = viewModel::endSession,
                onRetry = viewModel::retryConnection,
                onPermissionAction = viewModel::onPermissionAction,
                onCancel = onCancel
            )

            if (state.showEndConfirmation) {
                EndSessionDialog(
                    onConfirm = viewModel::confirmEndSession,
                    onDismiss = viewModel::cancelEndSession
                )
            }
        }
    }
}

@Composable
private fun SessionContent(
    state: SessionState,
    onEndSession: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    onPermissionAction:() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SessionHeader(
            status = state.status,
            duration = state.duration,
            errorMessage = state.errorMessage,
            modifier = Modifier.fillMaxHeight(0.4f)
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {

            AudioVisualizer(
                status = state.status,
                volume = state.volume
            )

            TranscriptOverlay(
                transcript = state.transcript,
                partialTranscript = state.partialTranscript,
                status = state.status
            )

            if (state.status == SessionStatus.ERROR) {
                val isPermissionError = state.microphonePermissionState != PermissionState.Granted
                
                ErrorOverlay(
                    errorMessage = state.errorMessage
                        ?: stringResource(Res.string.session_error_title),
                    onRetry = if (isPermissionError) onPermissionAction else onRetry,
                    onCancel = onCancel,
                    isPermissionError = isPermissionError
                )
            }
        }

        if (state.status != SessionStatus.ERROR) {
            SessionControls(
                status = state.status,
                onEndSession = onEndSession
            )
        }
    }
}



@Composable
fun AnalysisScreen() {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.session_processing_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.session_processing_body),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.fillMaxWidth(0.95f)
            )
        }
    }
}

@Preview
@Composable
fun SessionContentPreview() {
    TalkToMeTheme { 
        SessionContent(
            state = SessionState(),
            onEndSession = {},
            onRetry = {},
            onCancel = {},
            onPermissionAction = {}
        )
    }
}

@Preview
@Composable
fun AnalysisScreenPreview() {
    TalkToMeTheme {
        AnalysisScreen()
    }
}
