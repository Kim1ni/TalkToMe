package com.kmp.talktome.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.ui.composables.BaseHeader
import com.kmp.talktome.ui.screens.history.composables.BackgroundAmbience
import com.kmp.talktome.ui.screens.history.composables.CloudsContainer
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.history_count
import talktome.composeapp.generated.resources.history_title

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel<HistoryViewModel>(),
    onNavigateToSessionDetails: (Session) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        BackgroundAmbience()

        Column(modifier = Modifier.fillMaxSize()) {
            BaseHeader(
                title = stringResource(Res.string.history_title),
                subtitle = stringResource(Res.string.history_count, state.sessionCount)
            )

            CloudsContainer(
                sessions = state.groupedSessions,
                onSessionClick = onNavigateToSessionDetails
            )
        }
    }
}