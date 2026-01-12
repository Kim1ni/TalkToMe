package com.kmp.talktome.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmp.talktome.domain.repository.SessionRepository
import com.kmp.talktome.domain.usecase.login.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class HistoryViewModel (
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        loadSessions()
    }

    @OptIn(ExperimentalTime::class)
    private fun loadSessions() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                sessionRepository.getUserSessions(user?.uid ?: "")
                    .collect { sessions ->
                        _state.update {
                            it.copy(
                                sessionCount = sessions.size,
                                groupedSessions = sessions
                                    .sortedByDescending { session -> session.timestamp }
                                    .groupBy { session ->
                                        val tz = TimeZone.currentSystemDefault()
                                        Instant
                                            .fromEpochMilliseconds(session.timestamp)
                                            .toLocalDateTime(tz)
                                            .date
                                    }
                            )
                        }
                    }
            }
        }
    }
}
