package com.kmp.talktome.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmp.talktome.domain.model.NotificationSettings
import com.kmp.talktome.domain.model.Theme
import com.kmp.talktome.domain.notifications.NotificationManager
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.repository.PreferencesRepository
import com.kmp.talktome.domain.repository.SettingsRepository
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

private const val TAG = "ProfileViewModel"

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository,
    private val notificationManager: NotificationManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfileInformation()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun loadProfileInformation() {
        Napier.d("loadUserData called", tag = TAG)
        viewModelScope.launch {
            authRepository.currentUser.flatMapLatest { user ->
                Napier.d("Current user: ${user?.uid}", tag = TAG)
                if (user == null) {
                    flowOf(Pair(null, null))
                } else {
                    preferencesRepository.getUserPreferences(user.uid).flatMapLatest { preferences ->
                        flowOf(Pair(user, preferences))
                    }
                }
            }.collectLatest { (user, preferences) ->
                Napier.d("Updating state with user and preferences. ActivePersonaId: ${preferences?.activePersonaId}.", tag = TAG)
                _state.update {
                    it.copy(
                        user = user,
                        userName = user?.displayName ?: "Guest",
                        preferences = preferences
                    )
                }
            }
            settingsRepository.getTheme().collectLatest { theme ->
                _state.update {
                    it.copy(selectedTheme = theme)
                }
            }
            settingsRepository.getNotificationSettings().collectLatest { notificationSettings ->
                _state.update {
                    it.copy(notificationSettings = notificationSettings)
                }
            }
        }
    }

    fun updateName(newName: String) {
        Napier.d("updateName called with: $newName", tag = TAG)
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
        Napier.d("linkGoogleAccount called", tag = TAG)
        viewModelScope.launch {
            val result = authRepository.linkWithGoogle("")// TODO
            result.onSuccess {
                Napier.d("linkGoogleAccount success", tag = TAG)
            }.onError { error ->
                Napier.e("linkGoogleAccount error: ${error.message}", tag = TAG)
            }
        }
    }
    fun showThemeSelector() {
        _state.update { it.copy(showSelectThemeDialog = true) }
    }

    fun dismissThemeSelector() {
        _state.update { it.copy(showSelectThemeDialog = false) }
    }

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            Napier.d("setTheme called with: $theme", tag = TAG)
            settingsRepository.setTheme(theme = theme)
            _state.update {
                it.copy(selectedTheme = theme, showSelectThemeDialog = false)
            }
        }
    }

    fun setNotificationSettings(settings: NotificationSettings) {
        viewModelScope.launch {
            Napier.d("setNotificationSettings called with: $settings", tag = TAG)
            settingsRepository.setNotificationSettings(value = settings)
            _state.update { it.copy(notificationSettings = settings) }
        }
    }

    fun toggleNotificationSettings() {
        Napier.d("toggleNotificationSettings called", tag = TAG)
        viewModelScope.launch {
            val currentEnabled = _state.value.preferences?.notificationsEnabled ?: false

            if (!currentEnabled) {
                Napier.d("Enabling notifications", tag = TAG)
                // Request permission
                when {
                    notificationManager.areNotificationsBlocked() -> {
                        Napier.w("Notifications are blocked", tag = TAG)
                    }

                    notificationManager.hasNotificationPermission() -> {
                        updateNotificationPreference(true)
                        notificationManager.showTestNotification(
                            "TalkToMe",
                            "Notifications enabled! We'll gently remind you to check in."
                        )
                    }

                    else -> {
                        val granted = notificationManager.requestNotificationPermission()
                        Napier.d("Notification permission granted: $granted", tag = TAG)
                        if (granted) {
                            updateNotificationPreference(true)
                            notificationManager.showTestNotification(
                                "TalkToMe",
                                "Notifications enabled! We'll gently remind you to check in."
                            )
                        }
                    }
                }
            } else {
                Napier.d("Disabling notifications", tag = TAG)
                updateNotificationPreference(false)
            }
        }
    }

    private suspend fun updateNotificationPreference(enabled: Boolean) {
        val userId = _state.value.user?.uid ?: return
        Napier.d("Updating notification preference to: $enabled", tag = TAG)
        preferencesRepository.updatePreferences(
            userId = userId,
            updates = mapOf("notificationsEnabled" to enabled)
        )
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val currentTheme = _state.value.preferences?.theme ?: Theme.LIGHT
            val newTheme = if (currentTheme == Theme.LIGHT) Theme.DARK else Theme.LIGHT
            val userId = _state.value.user?.uid ?: return@launch

            Napier.d(tag = TAG, message = "Toggling theme to: $newTheme")
            preferencesRepository.updatePreferences(
                userId = userId,
                updates = mapOf("theme" to newTheme.name)
            )
        }
    }

    fun signOut() {
        Napier.d(tag = TAG, message = "signOut called")
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
