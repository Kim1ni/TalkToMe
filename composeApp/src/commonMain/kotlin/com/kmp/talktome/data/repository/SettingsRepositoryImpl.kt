package com.kmp.talktome.data.repository

import com.kmp.talktome.domain.model.NotificationSettings
import com.kmp.talktome.domain.model.Theme
import com.kmp.talktome.domain.repository.SettingsRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSettingsApi::class)
class SettingsRepositoryImpl(
    private val settings: ObservableSettings
): SettingsRepository {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private inline fun <reified T> String?.decodeOrNull(): T? {
        if (this == null) return null

        return try {
            json.decodeFromString<T>(this)
        } catch (_: SerializationException) {
            null
        }
    }

    override suspend fun setTheme(theme: Theme) = settings
        .set(Keys.THEME, theme.name)

    override suspend fun getTheme(): Flow<Theme> = settings.getStringOrNullFlow(Keys.THEME)
        .map { it?.let { Theme.valueOf(it) } ?: Theme.SYSTEM }

    override suspend fun getNotificationSettings(): Flow<NotificationSettings?> =
        settings.getStringOrNullFlow(Keys.NOTIFICATION_SETTINGS)
            .map { it.decodeOrNull<NotificationSettings>() }

    override suspend fun setNotificationSettings(value: NotificationSettings) = settings
        .set(Keys.NOTIFICATION_SETTINGS, json.encodeToString(value))

    private object Keys {
        const val THEME = "theme"
        const val NOTIFICATION_SETTINGS = "notificationSettings"
    }
}