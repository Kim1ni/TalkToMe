package com.kmp.talktome.ui.features.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.ui.features.history.composables.BackgroundAmbience
import com.kmp.talktome.ui.features.history.composables.CloudsContainer
import com.kmp.talktome.ui.features.history.composables.HistoryHeader
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel<HistoryViewModel>(),
    onSessionSelected: (Session) -> Unit,
    onBackPressed: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECE5DD))
    ) {
        // Background Ambience
        BackgroundAmbience()

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            HistoryHeader(
                sessionCount = state.sessions.size,
            )

            // Cloud Container
            CloudsContainer(
                sessions = state.sessions,
                onSessionClick = onSessionSelected
            )
        }
    }
}
