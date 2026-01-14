package com.kmp.talktome.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.DurationUnit

@Serializable
data class NotificationSettings(
    val enabled: Boolean = false,
    val defaultOffset: Duration = Duration.ZERO,
    val maxScheduled: Int = 20
)