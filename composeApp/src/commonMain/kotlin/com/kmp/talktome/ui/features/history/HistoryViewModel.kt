package com.kmp.talktome.ui.features.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmp.talktome.domain.repository.SessionRepository
import com.kmp.talktome.domain.usecase.login.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class HistoryViewModel (
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val sessionRepository: SessionRepository
) : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                sessionRepository.getUserSessions(user?.uid ?: "")
                    .collect { sessions ->
                        _state.update {
                            it.copy(
                                sessions = sessions.sortedByDescending { session ->
                                    session.timestamp
                                }
                            )
                        }
                    }
            }
        }
    }
}
