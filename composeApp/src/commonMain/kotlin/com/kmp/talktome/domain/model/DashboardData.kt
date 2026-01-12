package com.kmp.talktome.domain.model

data class DashboardData(
    val userName: String,
    val profilePictureUrl: String?,
    val sessions: List<Session>,
    val personas: List<CustomPersona>,
    val activePersonaId: String,
    val todos: List<TodoItem>,
    val isAnonymous: Boolean
)