package com.kmp.talktome.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmp.talktome.domain.model.CustomPersona
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.repository.PreferencesRepository
import com.kmp.talktome.ui.features.profile.ProfileState.Companion.DEFAULT_PERSONAS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository,
    //private val notificationManager: NotificationManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _state.update { it.copy(user = user) }

                combine(
                    authRepository.currentUser,
                    preferencesRepository.getUserPreferences(user?.uid ?: "")//.userPreferences
                ) { user, preferences ->
                    Pair(user, preferences)
                }.collect { (user, preferences) ->
                    _state.update {
                        it.copy(
                            user = user,
                            userName = user?.displayName ?: "Guest",
                            preferences = preferences,
                            allPersonas = DEFAULT_PERSONAS + (preferences?.customPersonas
                                ?: emptyList())
                        )
                    }
                }
            }
        }
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            authRepository.updateDisplayName(newName)
            _state.update { it.copy(userName = newName, isEditingName = false) }
        }
    }

    fun startEditingName() {
        _state.update {
            it.copy(
                isEditingName = true,
                tempName = it.userName
            )
        }
    }

    fun linkGoogleAccount() {
        /*
        viewModelScope.launch {
            val result = authRepository.linkWithGoogle()
            result.onSuccess {
                // Show success message
            }.onFailure { error ->
                // Show error message
            }
        }*/
    }

    fun toggleNotifications(/* context: Context */) {
        /*viewModelScope.launch {
            val currentEnabled = _state.value.preferences.notificationsEnabled

            if (!currentEnabled) {
                // Request permission
                when {
                    notificationManager.areNotificationsBlocked() -> {
                        // Show instructions dialog
                        showNotificationBlockedDialog(context)
                    }
                    notificationManager.hasNotificationPermission() -> {
                        // Permission already granted
                        updateNotificationPreference(true)
                        notificationManager.showTestNotification(
                            "TheraVoice",
                            "Notifications enabled! We'll gently remind you to check in."
                        )
                    }
                    else -> {
                        // Request permission
                        val granted = notificationManager.requestNotificationPermission(context)
                        if (granted) {
                            updateNotificationPreference(true)
                            /*TODO
                            notificationManager.showTestNotification(
                                "TheraVoice",
                                "Notifications enabled! We'll gently remind you to check in."
                            )*/
                        }
                    }
                }
            } else {
                // Turn off
                updateNotificationPreference(false)
            }
        }

         */
    }

    private suspend fun updateNotificationPreference(enabled: Boolean) {
        /*
        val updated = _state.value.preferences?.copy(notificationsEnabled = enabled)
        preferencesRepository.updatePreferences(updated)*/
    }

    fun toggleTheme() {/*
        viewModelScope.launch {
            val newTheme = if (_state.value.preferences?.theme == Theme.LIGHT)
                Theme.DARK else Theme.LIGHT

            val updated = _state.value.preferences?.copy(theme = newTheme)
            preferencesRepository.updatePreferences(updated)
        }*/
    }

    fun setActivePersona(id: String) {
        /*
        viewModelScope.launch {
            val updated = _state.value.preferences?.copy(activePersonaId = id)
            preferencesRepository.updatePreferences(updated)
        }*/
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
        if (persona.isDefault) return // Can't edit defaults

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
        /*
        val persona = _state.value.editingPersona ?: return

        if (persona.name.isBlank() || persona.instructions.isBlank()) {
            // Show validation error
            return
        }

        viewModelScope.launch {
            val currentPersonas = _state.value.preferences?.customPersonas
            val existingIndex = currentPersonas?.indexOfFirst { it.id == persona.id }

            val updatedPersonas = existingIndex?.let {
                if (it >= 0) {
                    currentPersonas.toMutableList().apply {
                        set(existingIndex, persona)
                    }
                } else {
                    currentPersonas + persona
                }
            }

            val updated = _state.value.preferences?.copy(customPersonas = updatedPersonas)
            preferencesRepository.updatePreferences(updated)

            dismissPersonaEditor()
        }*/
    }

    fun deletePersona(id: String) {
        /*
        viewModelScope.launch {
            val updatedPersonas = _state.value.preferences?.customPersonas
                ?.filter { it.id != id }

            var activeId = _state.value.preferences?.activePersonaId
            if (activeId == id) {
                activeId = "empathetic" // Reset to default
            }

            val updated = _state.value.preferences?.copy(
                customPersonas = updatedPersonas,
                activePersonaId = activeId
            )
            preferencesRepository.updatePreferences(updated)
        }*/
    }

    fun dismissPersonaEditor() {
        _state.update {
            it.copy(
                showPersonaEditor = false,
                editingPersona = null
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
