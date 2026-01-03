package com.kmp.talktome.ui.features.home

import MessageBarState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kmp.talktome.ui.features.home.composables.ActionPlanHeader
import com.kmp.talktome.ui.features.home.composables.EmptyTodosPlaceholder
import com.kmp.talktome.ui.features.home.composables.GuestAccountBanner
import com.kmp.talktome.ui.features.home.composables.HomeHeader
import com.kmp.talktome.ui.features.home.composables.InsightsGrid
import com.kmp.talktome.ui.features.home.composables.MoodTrendsCard
import com.kmp.talktome.ui.features.home.composables.TodoItemCard
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.home_start_session
import talktome.composeapp.generated.resources.icon_mic

@Composable
fun HomeScreen(
    mainPaddingValues: PaddingValues,
    viewModel: HomeViewModel = koinViewModel<HomeViewModel>(),
    onStartSession: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val messageBarState = remember { MessageBarState() }
    var idToken = ""

    state.toastMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            viewModel.dismissToast()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onStartSession,
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.icon_mic),
                        contentDescription = stringResource(Res.string.home_start_session),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(Res.string.home_start_session),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding() - 32.dp, bottom = paddingValues.calculateBottomPadding() - 4.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    HomeHeader(
                        userName = state.userName,
                        profilePictureUrl = state.profilePictureUrl
                    )
                }

                if (state.isAnonymous) {
                    item {
                        GoogleButtonUiContainerFirebase(
                            onResult = { result ->
                                result.onSuccess { user ->
                                    messageBarState.addSuccess("Logged in successfully")
                                    runBlocking { user?.getIdToken(true)?.let { idToken = it } }
                                    viewModel.linkAccount(idToken)
                                }.onFailure { error ->
                                    if(error.message?.contains("A network error") == true) {
                                        messageBarState.addError("No internet connection")
                                    } else if (error.message?.contains("idToken is null") == true) {
                                        messageBarState.addError("Google login failed")
                                    } else {
                                        messageBarState.addError(error.message ?: "Unknown error")
                                    }
                                }
                            },
                            linkAccount = true
                        ) {
                            GuestAccountBanner(
                                isLinking = state.isLinking,
                                onLinkAccount = { this@GoogleButtonUiContainerFirebase.onClick() }
                            )
                        }
                    }
                }

                if (state.chartData.size > 1) {
                    item {
                        MoodTrendsCard(
                            chartData = state.chartData,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                if (state.moodBoosters.isNotEmpty() || state.moodDrainers.isNotEmpty()) {
                    item {
                        InsightsGrid(
                            boosters = state.moodBoosters,
                            drainers = state.moodDrainers,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                item {
                    ActionPlanHeader(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                if (state.filteredTodos.isEmpty()) {
                    item {
                        EmptyTodosPlaceholder(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    items(state.filteredTodos) { todo ->
                        TodoItemCard(
                            todo = todo,
                            onToggle = { viewModel.toggleTodo(todo.id) },
                            onDelete = { viewModel.deleteTodo(todo.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}