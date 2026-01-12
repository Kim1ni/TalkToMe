package com.kmp.talktome.domain.model

enum class SessionStatus {
    IDLE,
    CONNECTING,
    ACTIVE,
    ENDING,
    ANALYZING,
    ENDED,
    ERROR,
    COMPLETED
}
