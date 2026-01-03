package com.kmp.talktome.domain.model

/**
 * User preferences and settings
 */
data class UserPreferences(
    val userId: String = "",
    val activePersonaId: String = "empathetic",
    val customPersonas: List<CustomPersona> = emptyList(),
    val notificationsEnabled: Boolean = false,
    val theme: Theme = Theme.LIGHT
) {
    /**
     * Get all personas (default + custom)
     */
    fun getAllPersonas(): List<CustomPersona> {
        return CustomPersona.getDefaults() + customPersonas
    }

    /**
     * Get active persona
     */
    fun getActivePersona(): CustomPersona? {
        return getAllPersonas().find { it.id == activePersonaId }
    }
}