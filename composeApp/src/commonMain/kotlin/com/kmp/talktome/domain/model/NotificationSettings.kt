package com.kmp.talktome.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettings(
    val session: Session
)