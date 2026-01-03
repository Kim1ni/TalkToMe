package com.kmp.talktome.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class TodoItem(
    val id: String = "",
    val userId: String = "",
    val text: String,
    val completed: Boolean = false,
    val sessionId: String? = null,
    val category: TodoCategory? = null,
    val reflection: TodoReflection? = null,
    val reminderSet: Boolean = false,
    val completedAt: Long? = null,
    val dueDateTimestamp: Long? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
) {
    // Helper methods
    fun isOverdue(): Boolean {
        // Add your logic for checking if task is overdue
        return false
    }

    fun getCategoryColor(): Int {
        return when (category) {
            TodoCategory.COGNITIVE -> 0xFF3F51B5.toInt() // Indigo
            TodoCategory.BEHAVIOURAL -> 0xFF2196F3.toInt() // Blue
            TodoCategory.SOCIAL -> 0xFFFF9800.toInt() // Orange
            null -> 0xFF9E9E9E.toInt() // Gray
        }
    }
}