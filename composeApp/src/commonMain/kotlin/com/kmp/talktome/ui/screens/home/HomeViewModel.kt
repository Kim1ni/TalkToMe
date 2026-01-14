package com.kmp.talktome.ui.screens.home

import androidx.lifecycle.ViewModel
import com.kmp.talktome.ui.screens.home.models.ChartDataPoint
import com.kmp.talktome.ui.screens.home.models.MoodInsight
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.model.TodoItem
import com.kmp.talktome.domain.model.TodoReflection
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.repository.PreferencesRepository
import com.kmp.talktome.domain.usecase.home.CalculateStreakUseCase
import com.kmp.talktome.domain.usecase.todo.DeleteTodoUseCase
import com.kmp.talktome.domain.usecase.home.GetDashboardDataUseCase
import com.kmp.talktome.domain.usecase.todo.ToggleTodoUseCase
import com.kmp.talktome.domain.util.onError
import com.kmp.talktome.domain.util.onSuccess
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class HomeViewModel(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val toggleTodoUseCase: ToggleTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository,
    private val viewModelScope: CoroutineScope
): ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        observeDashboardData()
    }

    private fun observeDashboardData() {
        viewModelScope.launch {
            getDashboardDataUseCase().collect { data ->
                if (data != null) {
                    val chartData = prepareChartData(data.sessions)
                    val insights = calculateMoodInsights(data.sessions)
                    val streak = calculateStreakUseCase(data.sessions)

                    _state.update {
                        it.copy(
                            userName = if (data.userName == "") { "Anonymous Guest" } else data.userName,
                            profilePictureUrl = data.profilePictureUrl,
                            sessions = data.sessions,
                            personas = data.personas,
                            activePersonaId = data.activePersonaId,
                            todos = data.todos,
                            isAnonymous = data.isAnonymous,
                            chartData = chartData,
                            moodBoosters = insights.boosters,
                            moodDrainers = insights.drainers,
                            currentStreak = streak,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onToggleTodo(todo: TodoItem) {
        if (todo.completed) {
            toggleTodo(todo.id)
        } else {
            _state.update { it.copy(activeReflectionTodoId = todo.id) }
        }
    }

    fun completeTodoWithReflection(todoId: String, mood: String, notes: String) {
        val reflection = TodoReflection(mood = mood, notes = notes)
        toggleTodo(todoId, reflection)
        dismissReflectionDialog()
    }

    private fun toggleTodo(todoId: String, reflection: TodoReflection? = null) {
        viewModelScope.launch {
            toggleTodoUseCase(todoId, reflection)
                .onSuccess {
                    showToast("Task updated!")
                }
                .onError { exception ->
                    showToast("Failed to update task: ${exception.message}")
                }
        }
    }

    fun deleteTodo(todoId: String) {
        viewModelScope.launch {
            deleteTodoUseCase(todoId)
                .onSuccess {
                    showToast("Task deleted")
                }
                .onError { exception ->
                    showToast("Failed to delete task: ${exception.message}")
                }
        }
    }

    fun setDateFilter(filter: String) {
        _state.update { it.copy(selectedDateFilter = filter) }
    }

    fun dismissReflectionDialog() {
        _state.update { it.copy(activeReflectionTodoId = null) }
    }

    fun handleGoogleSignInSuccess(user: FirebaseUser) {
        viewModelScope.launch {
            _state.update { it.copy(isLinking = true, errorMessage = null) }

            try {
                val idToken = user.getIdToken(true)
                if (idToken == null) {
                    _state.update {
                        it.copy(
                            isLinking = false,
                            errorMessage = "Failed to get authentication token"
                        )
                    }
                    showToast("Google login failed")
                    return@launch
                }

                authRepository.linkWithGoogle(idToken)
                    .onSuccess {
                        _state.update {
                            it.copy(
                                isLinking = false,
                                loginSuccess = true
                            )
                        }
                        showToast("Logged in successfully")
                    }
                    .onError { exception ->
                        handleLinkError(exception)
                    }
            } catch (e: Exception) {
                handleLinkError(e)
            }
        }
    }

    fun handleGoogleSignInFailure(error: Exception) {
        val errorMessage = when {
            error.message?.contains("network error", ignoreCase = true) == true ->
                "No internet connection"
            else -> error.message ?: "Unknown error"
        }
        _state.update { it.copy(errorMessage = errorMessage) }
        showToast(errorMessage)
    }

    private fun handleLinkError(exception: Exception) {
        val errorMessage = when {
            exception.message?.contains("network error", ignoreCase = true) == true ->
                "No internet connection"
            else -> exception.message ?: "Unknown error"
        }
        _state.update {
            it.copy(
                isLinking = false,
                errorMessage = errorMessage
            )
        }
        showToast(errorMessage)
    }

    fun startSessionWithPersona(personaId: String, onNavigate: () -> Unit) {
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            preferencesRepository.updatePreferences(userId, mapOf("activePersonaId" to personaId))
            onNavigate()
        }
    }

    fun dismissToast() {
        _state.update { it.copy(toastMessage = null) }
    }

    private fun showToast(message: String) {
        _state.update { it.copy(toastMessage = message) }
    }

    @OptIn(ExperimentalTime::class)
    private fun prepareChartData(sessions: List<Session>): List<ChartDataPoint> {
        return sessions
            .sortedBy { it.timestamp }
            .takeLast(10)
            .map { session ->
                ChartDataPoint(
                    date = session.timestamp,
                    sentimentScore = session.analysis?.sentimentScore?.toFloat() ?: 50f
                )
            }
    }

    private fun calculateMoodInsights(sessions: List<Session>): MoodInsights {
        val topicStats = mutableMapOf<String, TopicData>()

        sessions.forEach { session ->
            session.analysis?.topics?.forEach { topic ->
                val normalized = topic.lowercase().trim()
                val current = topicStats.getOrPut(normalized) {
                    TopicData(topic = normalized, totalScore = 0f, count = 0)
                }
                topicStats[normalized] = current.copy(
                    totalScore = current.totalScore + (session.analysis.sentimentScore),
                    count = current.count + 1
                )
            }
        }

        val topicItems = topicStats.values
            .map { data ->
                MoodInsight(
                    topic = data.topic.replaceFirstChar { it.uppercase() },
                    avgScore = (data.totalScore / data.count).toInt(),
                    count = data.count
                )
            }
            .filter { it.count >= 1 }
            .sortedBy { it.avgScore }

        val drainers = topicItems.take(3).filter { it.avgScore < 60 }
        val boosters = topicItems
            .sortedByDescending { it.avgScore }
            .take(3)
            .filter { it.avgScore >= 60 }

        return MoodInsights(boosters = boosters, drainers = drainers)
    }

    private data class TopicData(
        val topic: String,
        val totalScore: Float,
        val count: Int
    )

    private data class MoodInsights(
        val boosters: List<MoodInsight>,
        val drainers: List<MoodInsight>
    )
}