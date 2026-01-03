package com.kmp.talktome.domain.usecase.home

import com.kmp.talktome.domain.model.DashboardData
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.repository.SessionRepository
import com.kmp.talktome.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine


class GetDashboardDataUseCase(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
    private val todoRepository: TodoRepository
) {
    operator fun invoke(): Flow<DashboardData?> {
        return authRepository.currentUser.combine(
            combine(
                sessionRepository.getUserSessions(""), // Will be filtered by userId
                todoRepository.getUserTodos("")
            ) { sessions, todos -> Pair(sessions, todos) }
        ) { user, (sessions, todos) ->
            if (user == null) return@combine null

            // Filter by actual user ID
            val userSessions = sessions.filter { it.userId == user.uid }
            val userTodos = todos.filter { it.userId == user.uid }

            DashboardData(
                userName = user.displayName ?: "Guest",
                profilePictureUrl = user.photoUrl,
                sessions = userSessions,
                todos = userTodos,
                isAnonymous = user.isAnonymous
            )
        }
    }
}