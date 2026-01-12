package com.kmp.talktome.domain.model

import kotlinx.serialization.Serializable

/**
 * User preferences and settings
 */
@Serializable
data class UserPreferences(
    val userId: String = "",
    val activePersonaId: String = "empathetic",
    val customPersonas: List<CustomPersona> = emptyList(),
    val notificationsEnabled: Boolean = false,
    val theme: Theme = Theme.LIGHT
)