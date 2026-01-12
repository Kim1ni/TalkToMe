package com.kmp.talktome.ui.screens.persona

import com.kmp.talktome.domain.model.CustomPersona

data class PersonaState(
    val personas: List<CustomPersona> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showPersonaEditor: Boolean = false,
    val editingPersona: CustomPersona? = null,
    val userId: String? = null
)
