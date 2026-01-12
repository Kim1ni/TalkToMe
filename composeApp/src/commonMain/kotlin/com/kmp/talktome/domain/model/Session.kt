package com.kmp.talktome.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Therapy session with full data
 */
@Serializable
data class Session @OptIn(ExperimentalTime::class) constructor(
    val id: String = "",
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val durationSeconds: Int = 0,
    val analysis: SessionAnalysis? = null,
    val transcript: List<TranscriptMessage> = emptyList(),
    val audioStoragePath: String? = null,
    val userId: String = ""
)
