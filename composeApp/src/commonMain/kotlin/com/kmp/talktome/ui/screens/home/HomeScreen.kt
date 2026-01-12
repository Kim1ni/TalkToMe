package com.kmp.talktome.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kmp.talktome.ui.composables.LoadingScreen
import com.kmp.talktome.ui.screens.home.composables.ActionPlanHeader
import com.kmp.talktome.ui.screens.home.composables.EmptyTodosPlaceholder
import com.kmp.talktome.ui.screens.home.composables.GuestAccountBanner
import com.kmp.talktome.ui.screens.home.composables.HomeHeader
import com.kmp.talktome.ui.screens.home.composables.InsightsGrid
import com.kmp.talktome.ui.screens.home.composables.MoodTrendsCard
import com.kmp.talktome.ui.screens.home.composables.PersonaRowCard
import com.kmp.talktome.ui.screens.home.composables.TodoItemCard
import com.kmp.talktome.ui.screens.home.models.CardStackPosition
import com.kmp.talktome.ui.screens.home.models.CardStackPosition.BOTTOM
import com.kmp.talktome.ui.screens.home.models.CardStackPosition.MIDDLE
import com.kmp.talktome.ui.screens.home.models.CardStackPosition.SINGLE
import com.kmp.talktome.ui.screens.home.models.CardStackPosition.TOP
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_arrowback
import talktome.composeapp.generated.resources.icon_quote
import talktome.composeapp.generated.resources.mood_good
import talktome.composeapp.generated.resources.mood_hard
import talktome.composeapp.generated.resources.mood_neutral
import talktome.composeapp.generated.resources.mood_proud
import talktome.composeapp.generated.resources.mood_relieved
import talktome.composeapp.generated.resources.reflection_notes_hint
import talktome.composeapp.generated.resources.reflection_notes_label
import talktome.composeapp.generated.resources.reflection_question
import talktome.composeapp.generated.resources.reflection_save
import talktome.composeapp.generated.resources.reflection_title

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel<HomeViewModel>(),
    onStartSession: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var completedExpanded by remember { mutableStateOf(false) }

    state.toastMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.dismissToast()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (state.isLoading) {
            LoadingScreen()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding() - 32.dp,
                    bottom = 16.dp
                )
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
                                    user?.let { viewModel.handleGoogleSignInSuccess(it ) }
                                }.onFailure { error ->
                                    viewModel.handleGoogleSignInFailure(error as Exception)
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

                if (state.personas.isNotEmpty()) {
                    item {
                        PersonaRowCard(
                            personas = state.personas,
                            activePersonaId = state.activePersonaId,
                            onPersonaClick = { personaId ->
                                viewModel.startSessionWithPersona(personaId, onStartSession)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
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

                if (state.activeTodos.isEmpty() && state.completedTodos.isEmpty()) {
                    item {
                        EmptyTodosPlaceholder(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    // Active Todos
                    itemsIndexed(state.activeTodos) { index, todo ->
                        val position = getPosition(index, state.activeTodos)
                        TodoItemCard(
                            todo = todo,
                            onToggle = { viewModel.onToggleTodo(todo) },
                            onDelete = { viewModel.deleteTodo(todo.id) },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(
                                    top = if (position == TOP || position == SINGLE) 8.dp else 0.dp,
                                    bottom = if (position == BOTTOM || position == SINGLE) 8.dp else 0.dp
                                ),
                            position = position
                        )
                    }

                    // Completed Section Header
                    if (state.completedTodos.isNotEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clickable { completedExpanded = !completedExpanded },
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Completed",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(Modifier.size(8.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = MaterialTheme.shapes.extraSmall
                                        ) {
                                            Text(
                                                text = state.completedTodos.size.toString(),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }
                                    Icon(
                                        painter = painterResource(Res.drawable.icon_arrowback),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .rotate(if (completedExpanded) 90f else 270f),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Completed Todos (Animated)
                        item {
                            AnimatedVisibility(
                                visible = completedExpanded,
                                enter = fadeIn() + expandVertically(animationSpec = spring()),
                                exit = fadeOut() + shrinkVertically(animationSpec = spring())
                            ) {
                                Column {
                                    state.completedTodos.forEachIndexed { index, todo ->
                                        val position = getPosition(index, state.completedTodos)
                                        TodoItemCard(
                                            todo = todo,
                                            onToggle = { viewModel.onToggleTodo(todo) },
                                            onDelete = { viewModel.deleteTodo(todo.id) },
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .padding(
                                                    top = if (position == TOP || position == SINGLE) 4.dp else 0.dp,
                                                    bottom = if (position == BOTTOM || position == SINGLE) 4.dp else 0.dp
                                                ),
                                            position = position
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            state.activeReflectionTodoId?.let { todoId ->
                AddToDoReflectionDialog(
                    onDismiss = viewModel::dismissReflectionDialog,
                    onConfirm = { mood, notes ->
                        viewModel.completeTodoWithReflection(todoId, mood, notes)
                    }
                )
            }
        }
    }
}

@Composable
fun AddToDoReflectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    var mood by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val moodOptions = listOf(
        stringResource(Res.string.mood_hard),
        stringResource(Res.string.mood_neutral),
        stringResource(Res.string.mood_good),
        stringResource(Res.string.mood_relieved),
        stringResource(Res.string.mood_proud)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(Res.string.reflection_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = stringResource(Res.string.reflection_question),
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    moodOptions.forEach { option ->
                        FilterChip(
                            selected = mood == option,
                            onClick = { mood = option },
                            label = { Text(option, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(Res.string.reflection_notes_label)) },
                    placeholder = { Text(stringResource(Res.string.reflection_notes_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(mood, notes) },
                enabled = mood.isNotEmpty()
            ) {
                Text(text = stringResource(Res.string.reflection_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        icon = {
            Icon(
                painter = painterResource(Res.drawable.icon_quote),
                contentDescription = null
            )
        },
    )
}

fun getPosition(index: Int, items: List<Any>): CardStackPosition = when (true) {
    (index == 0 && items.size > 1) -> TOP
    (index == items.lastIndex && items.size > 1) -> BOTTOM
    (items.size == 1 && index == 0) -> SINGLE
    else -> MIDDLE
}