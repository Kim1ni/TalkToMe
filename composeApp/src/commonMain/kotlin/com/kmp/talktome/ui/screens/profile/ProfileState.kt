package com.kmp.talktome.ui.screens.profile

import com.kmp.talktome.domain.model.NotificationSettings
import com.kmp.talktome.domain.model.Theme
import com.kmp.talktome.domain.model.User
import com.kmp.talktome.domain.model.UserPreferences

data class ProfileState(
    val user: User? = null,
    val userName: String = "Guest",
    val isEditingName: Boolean = false,
    val tempName: String = "",
    val preferences: UserPreferences? = UserPreferences(),
    val isNotificationsEnabled: Boolean = false,
    val selectedTheme: Theme = Theme.SYSTEM,
    val notificationSettings: NotificationSettings? = NotificationSettings(),
    val showSelectThemeDialog: Boolean = false
)
