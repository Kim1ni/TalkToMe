package com.kmp.talktome.ui.screens.history

import com.kmp.talktome.domain.model.Session
import kotlinx.datetime.LocalDate

data class HistoryState(
    val groupedSessions: Map<LocalDate, List<Session>> = emptyMap(),
    val sessionCount: Int = 0,
    val isLoading: Boolean = false
)