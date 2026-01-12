package com.kmp.talktome.ui.screens.persona

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmp.talktome.domain.model.CustomPersona
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.repository.PersonaRepository
import com.kmp.talktome.domain.repository.PreferencesRepository
import com.kmp.talktome.domain.util.onError
import com.kmp.talktome.domain.util.onSuccess
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TAG = "PersonaViewModel"

class PersonaViewModel(
    private val authRepository: AuthRepository,
    private val personaRepository: PersonaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PersonaState())
    val state: StateFlow<PersonaState> = _state.asStateFlow()

    init {
        loadData()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun loadData() {
        viewModelScope.launch {
            authRepository.currentUser.flatMapLatest { user ->
                if (user == null) {
                    _state.update { it.copy(userId = null, personas = emptyList()) }
                    flowOf(null)
                } else {
                    _state.update { it.copy(userId = user.uid) }
                    personaRepository.getPersonas(user.uid).flatMapLatest { personas ->
                        _state.update {
                            it.copy(
                                personas = personas
                            )
                        }
                        flowOf(Unit)
                    }
                }
            }.collectLatest { }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun startCreatingPersona() {
        _state.update {
            it.copy(
                showPersonaEditor = true,
                editingPersona = CustomPersona.empty().copy(
                    id = Clock.System.now().toEpochMilliseconds().toString()
                )
            )
        }
    }

    fun startEditingPersona(persona: CustomPersona) {
        if (persona.isDefault) return
        _state.update {
            it.copy(
                showPersonaEditor = true,
                editingPersona = persona
            )
        }
    }

    fun updateEditingPersona(persona: CustomPersona) {
        _state.update { it.copy(editingPersona = persona) }
    }

    fun savePersona() {
        val userId = _state.value.userId ?: return
        val persona = _state.value.editingPersona ?: return

        if (persona.name.isBlank() || persona.instructions.isBlank()) {
            return
        }

        viewModelScope.launch {
            val isExisting = _state.value.personas.any { it.id == persona.id && !it.isDefault }
            val result = if (isExisting) {
                personaRepository.updatePersona(userId, persona)
            } else {
                personaRepository.addPersona(userId, persona)
            }

            result.onSuccess {
                dismissPersonaEditor()
            }.onError { error ->
                Napier.e(tag = TAG, message = "Failed to save persona: ${error.message}")
            }
        }
    }

    fun deletePersona(id: String) {
        val userId = _state.value.userId ?: return
        viewModelScope.launch {
            personaRepository.deletePersona(userId, id)
        }
    }

    fun dismissPersonaEditor() {
        _state.update {
            it.copy(
                showPersonaEditor = false,
                editingPersona = null
            )
        }
    }
}
