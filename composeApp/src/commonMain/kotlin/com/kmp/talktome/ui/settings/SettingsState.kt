package com.kmp.talktome.ui.settings

import com.kmp.talktome.domain.model.Theme

data class SettingsState (
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val hasNotificationPermission: Boolean = false
)