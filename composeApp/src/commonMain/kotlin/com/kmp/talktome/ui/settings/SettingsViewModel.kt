package com.kmp.talktome.ui.settings

import androidx.lifecycle.ViewModel
import com.kmp.talktome.PermissionHandler
import com.kmp.talktome.di.viewModelScope
import com.kmp.talktome.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val permissionHandler: PermissionHandler
): ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            combine(
                repository.observeDarkMode(),
                repository.observeNotificationsEnabled(),
                permissionHandler.observeNotificationPermission()
            ) { darkMode, notifications, hasPermission ->
                SettingsState(
                    isDarkMode = darkMode,
                    notificationsEnabled = notifications,
                    hasNotificationPermission = hasPermission
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            repository.setDarkMode(!_state.value.isDarkMode)
        }
    }

    suspend fun toggleNotifications() {
        val shouldEnable = !_state.value.notificationsEnabled

        if (shouldEnable) {
            // Request permission first if not granted
            if (!_state.value.hasNotificationPermission) {
                val granted = permissionHandler.requestNotificationPermission()
                if (!granted) return
            }
        }

        repository.setNotificationsEnabled(shouldEnable)
    }
}
