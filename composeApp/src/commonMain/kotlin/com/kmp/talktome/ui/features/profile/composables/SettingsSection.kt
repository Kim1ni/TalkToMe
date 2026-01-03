package com.kmp.talktome.ui.features.profile.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.Theme
import com.kmp.talktome.domain.model.UserPreferences
import org.jetbrains.compose.resources.painterResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_dark_mode
import talktome.composeapp.generated.resources.icon_light_mode
import talktome.composeapp.generated.resources.icon_link
import talktome.composeapp.generated.resources.icon_notifications

@Composable
fun SettingsSection(
    preferences: UserPreferences?,
    onNotificationToggle: () -> Unit,
    onThemeToggle: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF5F5F5))
    ) {
        Column {
            // Notifications Toggle
            SettingItem(
                painter = painterResource(Res.drawable.icon_notifications),
                iconColor = if (preferences?.notificationsEnabled ?: false)
                    Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                iconBackground = if (preferences?.notificationsEnabled ?: false)
                    Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                title = "Notifications",
                subtitle = if (preferences?.notificationsEnabled ?: false)
                    "On: Gentle check-ins allowed"
                else
                    "Off: Browser permissions needed",
                onClick = onNotificationToggle,
                trailing = {
                    Switch(
                        checked = preferences?.notificationsEnabled ?: false,
                        onCheckedChange = { onNotificationToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF128C7E),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFBDBDBD)
                        )
                    )
                },
                showDivider = true
            )

            // Theme Toggle
            SettingItem(
                painter = if (preferences?.theme == Theme.DARK)
                    painterResource(Res.drawable.icon_dark_mode) else painterResource(Res.drawable.icon_light_mode),
                iconColor = Color(0xFF5C6BC0),
                iconBackground = Color(0xFFE8EAF6),
                title = "Appearance",
                subtitle = if (preferences?.theme == Theme.DARK)
                    "Dark Mode (Inverted)"
                else
                    "Light Mode (Default)",
                onClick = onThemeToggle,
                trailing = {
                    Icon(// TODO Add chevron right icon
                        painter = painterResource(Res.drawable.icon_link),
                        contentDescription = null,
                        tint = Color(0xFFE0E0E0),
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }
    }
}
