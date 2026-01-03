package com.kmp.talktome.ui.features.home

import androidx.lifecycle.ViewModel
import com.kmp.talktome.domain.model.ChartDataPoint
import com.kmp.talktome.domain.model.MoodInsight
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.model.TodoReflection
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.usecase.home.CalculateStreakUseCase
import com.kmp.talktome.domain.usecase.home.DeleteTodoUseCase
import com.kmp.talktome.domain.usecase.home.GetDashboardDataUseCase
import com.kmp.talktome.domain.usecase.home.ToggleTodoUseCase
import com.kmp.talktome.domain.util.onError
import com.kmp.talktome.domain.util.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class HomeViewModel(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val toggleTodoUseCase: ToggleTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val authRepository: AuthRepository,
    private val viewModelScope: CoroutineScope
): ViewModel(), KoinComponent {
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

    fun toggleTodo(todoId: String, reflection: TodoReflection? = null) {
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

    fun setActiveReflectionTodo(todoId: String?) {
        _state.update { it.copy(activeReflectionTodoId = todoId) }
    }

    fun linkAccount(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLinking = true) }

            authRepository.linkWithGoogle(idToken)

            // Platform-specific linking would happen here
            // For now, just simulate
            kotlinx.coroutines.delay(1000)

            _state.update { it.copy(isLinking = false) }
            showToast("Account linking not yet implemented")
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
                val date = Instant.fromEpochMilliseconds(session.timestamp)
                    .toLocalDateTime(TimeZone.currentSystemDefault())

                ChartDataPoint(
                    date = "${date.month.name.take(3)} ${date.day}",
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