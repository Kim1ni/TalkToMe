package com.kmp.talktome.ui.features.home

import com.kmp.talktome.domain.model.ChartDataPoint
import com.kmp.talktome.domain.model.MoodInsight
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.model.TodoItem

data class HomeState(
    val userName: String = "Guest",
    val isAnonymous: Boolean = false,
    val isLoading: Boolean = true,
    val sessions: List<Session> = emptyList(),
    val todos: List<TodoItem> = emptyList(),
    val chartData: List<ChartDataPoint> = emptyList(),
    val moodBoosters: List<MoodInsight> = emptyList(),
    val moodDrainers: List<MoodInsight> = emptyList(),
    val profilePictureUrl: String? = null,
    val currentStreak: Int = 0,
    val selectedDateFilter: String = "all",
    val activeReflectionTodoId: String? = null,
    val isLinking: Boolean = false,
    val toastMessage: String? = null
) {
    val activeTodos: List<TodoItem>
        get() = todos.filter { !it.completed }

    val completedTodos: List<TodoItem>
        get() = todos.filter { it.completed }

    val filteredTodos: List<TodoItem>
        get() = when (selectedDateFilter) {
            "all" -> todos
            else -> todos.filter { todo ->
                // Filter by date logic
                todo.sessionId == selectedDateFilter
            }
        }
}
