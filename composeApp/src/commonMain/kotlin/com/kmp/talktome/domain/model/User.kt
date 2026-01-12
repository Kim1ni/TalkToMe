package com.kmp.talktome.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class User @OptIn(ExperimentalTime::class) constructor(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isAnonymous: Boolean = false,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)