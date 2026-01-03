package com.kmp.talktome.ui.features.history

import com.kmp.talktome.domain.model.Session

data class HistoryState(
    val sessions: List<Session> = emptyList(),
    val isLoading: Boolean = false
)