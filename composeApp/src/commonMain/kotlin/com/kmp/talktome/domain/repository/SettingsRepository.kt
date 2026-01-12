package com.kmp.talktome.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun getDarkMode(): Boolean
    suspend fun observeDarkMode(): Flow<Boolean>

    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun getNotificationsEnabled(): Boolean
    suspend fun observeNotificationsEnabled(): Flow<Boolean>
}
