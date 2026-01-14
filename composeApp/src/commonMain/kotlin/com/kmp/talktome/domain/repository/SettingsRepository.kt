package com.kmp.talktome.domain.repository

import com.kmp.talktome.domain.model.NotificationSettings
import com.kmp.talktome.domain.model.Theme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setTheme(theme: Theme)
    fun getTheme(): Flow<Theme>

    suspend fun setNotificationSettings(value: NotificationSettings)
    fun getNotificationSettings(): Flow<NotificationSettings?>

}
