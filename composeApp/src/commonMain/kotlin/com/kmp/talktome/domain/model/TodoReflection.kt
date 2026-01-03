package com.kmp.talktome.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock

/**
 * User's reflection after completing a task
 */
@Serializable
data class TodoReflection(
    val mood: String,
    val notes: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)