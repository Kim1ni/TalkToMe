package com.kmp.talktome.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isAnonymous: Boolean = false,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)