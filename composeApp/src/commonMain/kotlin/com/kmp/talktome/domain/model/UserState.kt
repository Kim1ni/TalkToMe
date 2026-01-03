package com.kmp.talktome.domain.model

/**
 * Complete user state
 */
data class UserState(
    val sessions: List<Session> = emptyList(),
    val todos: List<TodoItem> = emptyList(),
    val user: User,
    val preferences: UserPreferences = UserPreferences()
) {
    /**
     * Get active (incomplete) todos
     */
    fun getActiveTodos(): List<TodoItem> {
        return todos.filter { !it.completed }
    }

    /**
     * Get completed todos
     */
    fun getCompletedTodos(): List<TodoItem> {
        return todos.filter { it.completed }
    }

    /**
     * Get recent sessions
     */
    fun getRecentSessions(limit: Int = 10): List<Session> {
        return sessions
            .sortedByDescending { it.timestamp }
            .take(limit)
    }

    /**
     * Calculate total session time
     */
    fun getTotalSessionTime(): Int {
        return sessions.sumOf { it.durationSeconds }
    }

    /**
     * Get average sentiment score
     */
    fun getAverageSentiment(): Float {
        val scores = sessions.mapNotNull { it.analysis?.sentimentScore }
        return if (scores.isNotEmpty()) {
            scores.average().toFloat()
        } else {
            50f
        }
    }
}