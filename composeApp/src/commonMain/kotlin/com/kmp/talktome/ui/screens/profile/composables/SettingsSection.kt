package com.kmp.talktome.ui.screens.profile.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.Theme
import com.kmp.talktome.domain.model.UserPreferences
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_arrowback
import talktome.composeapp.generated.resources.icon_dark_mode
import talktome.composeapp.generated.resources.icon_light_mode
import talktome.composeapp.generated.resources.icon_notifications
import talktome.composeapp.generated.resources.profile_appearance
import talktome.composeapp.generated.resources.profile_dark_mode
import talktome.composeapp.generated.resources.profile_light_mode
import talktome.composeapp.generated.resources.profile_notifications

@Composable
fun SettingsSection(
    preferences: UserPreferences?,
    onNotificationToggle: () -> Unit,
    onThemeClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            // Notifications Toggle
            SettingItem(
                painter = painterResource(Res.drawable.icon_notifications),
                iconColor = if (preferences?.notificationsEnabled ?: false)
                    MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                iconBackground = if (preferences?.notificationsEnabled ?: false)
                    MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                title = stringResource(Res.string.profile_notifications),
                subtitle = if (preferences?.notificationsEnabled ?: false)
                    "On: Gentle check-ins allowed"
                else
                    "Off: Permissions needed",
                onClick = onNotificationToggle,
                trailing = {
                    Switch(
                        checked = preferences?.notificationsEnabled ?: false,
                        onCheckedChange = { onNotificationToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                },
                showDivider = true
            )

            // Theme Selector
            SettingItem(
                painter = when (preferences?.theme) {
                    Theme.DARK -> painterResource(Res.drawable.icon_dark_mode)
                    Theme.LIGHT -> painterResource(Res.drawable.icon_light_mode)
                    else -> painterResource(Res.drawable.icon_light_mode) // Add a system icon if you have one
                },
                iconColor = MaterialTheme.colorScheme.tertiary,
                iconBackground = MaterialTheme.colorScheme.tertiaryContainer,
                title = stringResource(Res.string.profile_appearance),
                subtitle = when (preferences?.theme) {
                    Theme.DARK -> "Dark Mode"
                    Theme.LIGHT -> "Light Mode"
                    Theme.SYSTEM -> "Follow System"
                    null -> "System Default"
                },
                onClick = onThemeClick,
                trailing = {
                    Icon(
                        painter = painterResource(Res.drawable.icon_arrowback),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(20.dp).graphicsLayer(rotationZ = 180f)
                    )
                }
            )
        }
    }
}
