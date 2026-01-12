package com.kmp.talktome.data.repository

import com.kmp.talktome.domain.repository.SettingsRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toSuspendSettings
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class)
class SettingsRepositoryImpl(
    settings: Settings
): SettingsRepository {
    private val flowSettings: FlowSettings = settings.toSuspendSettings() as FlowSettings

    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
    }

    // Dark Mode
    override suspend fun setDarkMode(enabled: Boolean) {
        flowSettings.putBoolean(KEY_DARK_MODE, enabled)
    }

    override suspend fun getDarkMode(): Boolean {
        return flowSettings.getBoolean(KEY_DARK_MODE, defaultValue = false)
    }

    override suspend fun observeDarkMode(): Flow<Boolean> {
        return flowSettings.getBooleanFlow(KEY_DARK_MODE, defaultValue = false)
    }

    // Notifications
    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        flowSettings.putBoolean(KEY_NOTIFICATIONS, enabled)
    }

    override suspend fun getNotificationsEnabled(): Boolean {
        return flowSettings.getBoolean(KEY_NOTIFICATIONS, defaultValue = true)
    }

    override suspend fun observeNotificationsEnabled(): Flow<Boolean> {
        return flowSettings.getBooleanFlow(KEY_NOTIFICATIONS, defaultValue = true)
    }
}