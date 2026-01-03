package com.kmp.talktome.domain.model

/**
 * Voice options for AI therapist
 */
enum class AIVoice {
    PUCK, CHARON, KORE, FENRIR, AOEDE;

    companion object {
        fun fromString(value: String?): AIVoice {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: PUCK
        }
    }

    fun toDisplayString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}
