package com.kmp.talktome.domain.usecase.home

import com.kmp.talktome.domain.model.DashboardData
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.repository.PersonaRepository
import com.kmp.talktome.domain.repository.PreferencesRepository
import com.kmp.talktome.domain.repository.SessionRepository
import com.kmp.talktome.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf


class GetDashboardDataUseCase(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
    private val todoRepository: TodoRepository,
    private val personaRepository: PersonaRepository,
    private val preferencesRepository: PreferencesRepository
) {
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<DashboardData?> {
        return authRepository.currentUser.flatMapLatest { user ->
            if (user == null) return@flatMapLatest flowOf(null)
            val userId = user.uid

            combine(
                sessionRepository.getUserSessions(userId = userId),
                todoRepository.getUserTodos(userId = userId),
                personaRepository.getPersonas(userId = userId),
                preferencesRepository.getUserPreferences(userId = userId)
            ) { sessions, todos, personas, prefs ->
                DashboardData(
                    userName = user.displayName ?: "Anonymous Friend",
                    profilePictureUrl = user.photoUrl,
                    sessions = sessions.filter { it.userId == userId },
                    personas = personas,
                    activePersonaId = prefs?.activePersonaId ?: "empathetic",
                    todos = todos.filter { it.userId == userId },
                    isAnonymous = user.isAnonymous
                )
            }
        }
    }
}