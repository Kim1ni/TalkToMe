package com.kmp.talktome.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.Theme
import com.kmp.talktome.ui.composables.BaseHeader
import com.kmp.talktome.ui.screens.profile.composables.IdentitySection
import com.kmp.talktome.ui.screens.profile.composables.SettingItem
import com.kmp.talktome.ui.screens.profile.composables.SettingsSection
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_arrowback
import talktome.composeapp.generated.resources.icon_check
import talktome.composeapp.generated.resources.icon_sign_out
import talktome.composeapp.generated.resources.profile_care_team
import talktome.composeapp.generated.resources.profile_care_team_subtitle
import talktome.composeapp.generated.resources.profile_sign_out
import talktome.composeapp.generated.resources.profile_title
import talktome.composeapp.generated.resources.profile_version_footer
import kotlin.text.forEach
import kotlin.text.lowercase

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel<ProfileViewModel>(),
    onNavigateToPersonas: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        ProfileSettingsContent(
            state = state,
            onUpdateName = viewModel::updateName,
            onStartEditName = viewModel::startEditingName,
            onLinkAccount = viewModel::linkGoogleAccount,
            onNotificationToggle = {},//viewModel::toggleNotifications,
            onThemeClick = viewModel::showThemeSelector,
            onNavigateToPersonas = onNavigateToPersonas,
            onSignOut = viewModel::signOut
        )
    }
    if (state.showSelectThemeDialog) {
        ThemeSelectionDialog(
            onDismiss = viewModel::dismissThemeSelector,
            onThemeSelected = viewModel::setTheme,
            selectedTheme = state.selectedTheme
        )
    }
}

@Composable
private fun ProfileSettingsContent(
    state: ProfileState,
    onUpdateName: (String) -> Unit,
    onStartEditName: () -> Unit,
    onLinkAccount: () -> Unit,
    onNotificationToggle: () -> Unit,
    onThemeClick: () -> Unit,
    onNavigateToPersonas: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BaseHeader(
            title = stringResource(Res.string.profile_title)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                IdentitySection(
                    user = state.user,
                    userName = state.userName,
                    isEditingName = state.isEditingName,
                    tempName = state.tempName,
                    onStartEditName = onStartEditName,
                    onSaveName = { onUpdateName(it) },
                    onLinkAccount = onLinkAccount
                )
            }

            // Manage Therapists Section
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    SettingItem(
                        painter = painterResource(Res.drawable.icon_check),
                        iconColor = MaterialTheme.colorScheme.primary,
                        iconBackground = MaterialTheme.colorScheme.primaryContainer,
                        title = stringResource(Res.string.profile_care_team),
                        subtitle = stringResource(Res.string.profile_care_team_subtitle),
                        onClick = onNavigateToPersonas,
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

            // Settings Section
            item {
                SettingsSection(
                    preferences = state.preferences,
                    onNotificationToggle = onNotificationToggle,
                    onThemeClick = onThemeClick
                )
            }

            // Sign Out Button
            item {
                Button(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.icon_sign_out),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.profile_sign_out),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Footer
            item {
                Text(
                    text = stringResource(Res.string.profile_version_footer),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    onDismiss: () -> Unit,
    onThemeSelected: (Theme) -> Unit,
    selectedTheme: Theme,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Theme") },
        text = {
            Column {
                Theme.entries.forEach { theme ->
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            onThemeSelected(theme)
                            onDismiss()
                        }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = theme == selectedTheme, onClick = null)
                        Spacer(Modifier.width(12.dp))
                        Text(theme.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )

}



