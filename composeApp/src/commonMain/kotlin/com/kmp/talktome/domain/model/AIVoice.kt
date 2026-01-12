package com.kmp.talktome.domain.model

/**
 * Voice options for AI therapist
 */
enum class AIVoice {
    PUCK, CHARON, KORE, FENRIR, AOEDE;

    fun toDisplayString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}
