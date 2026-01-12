package com.kmp.talktome.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class TranscriptMessage @OptIn(ExperimentalTime::class) constructor(
    val role: TranscriptRole,
    val text: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)

enum class TranscriptRole {
    USER, MODEL;

    companion object {
        fun fromString(value: String?): TranscriptRole? {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            }
        }
    }
}