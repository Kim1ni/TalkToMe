package com.kmp.talktome.domain.model

enum class SessionStatus {
    IDLE,
    CONNECTING,
    ACTIVE,
    ENDING,
    ENDED,
    ERROR,
    COMPLETED
}
