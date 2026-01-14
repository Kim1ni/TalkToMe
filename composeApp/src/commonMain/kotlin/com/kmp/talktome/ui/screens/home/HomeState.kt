package com.kmp.talktome.ui.screens.home

import com.kmp.talktome.ui.screens.home.models.ChartDataPoint
import com.kmp.talktome.domain.model.CustomPersona
import com.kmp.talktome.ui.screens.home.models.MoodInsight
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.model.TodoItem

data class HomeState(
    val userName: String = "Guest",
    val profilePictureUrl: String? = null,
    val isAnonymous: Boolean = false,
    val isLoading: Boolean = true,
    val isLinking: Boolean = false,
    val sessions: List<Session> = emptyList(),
    val personas: List<CustomPersona> = emptyList(),
    val activePersonaId: String = "",
    val todos: List<TodoItem> = emptyList(),
    val chartData: List<ChartDataPoint> = emptyList(),
    val moodBoosters: List<MoodInsight> = emptyList(),
    val moodDrainers: List<MoodInsight> = emptyList(),
    val currentStreak: Int = 0,
    val selectedDateFilter: String = "all",
    val activeReflectionTodoId: String? = null,
    val toastMessage: String? = null,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false
) {
    val activeTodos: List<TodoItem>
        get() = todos.filter { !it.completed }

    val completedTodos: List<TodoItem>
        get() = todos.filter { it.completed }

    val filteredTodos: List<TodoItem>
        get() = when (selectedDateFilter) {
            "all" -> todos
            else -> todos.filter { todo ->
                todo.sessionId == selectedDateFilter
            }
        }
}
