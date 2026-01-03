package com.kmp.talktome.ui.features.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.CustomPersona
import com.kmp.talktome.domain.model.Theme
import com.kmp.talktome.ui.features.profile.composables.CareTeamSection
import com.kmp.talktome.ui.features.profile.composables.IdentitySection
import com.kmp.talktome.ui.features.profile.composables.PersonaEditorModal
import com.kmp.talktome.ui.features.profile.composables.SettingsSection
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_sign_out

/*
@Composable
fun ProfileScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Profile Screen coming soon.")
    }

}*/

// ProfileSettingsScreen.kt
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel<ProfileViewModel>(),
) {
    val state by viewModel.state.collectAsState()
    /* val context = LocalContext.current */

    Box(modifier = Modifier.fillMaxSize()) {
        ProfileSettingsContent(
            state = state,
            onUpdateName = { viewModel.updateName(it) },
            onLinkAccount = { viewModel.linkGoogleAccount() },
            onNotificationToggle = { viewModel.toggleNotifications(/*context*/) },
            onThemeToggle = { viewModel.toggleTheme() },
            onSelectPersona = { viewModel.setActivePersona(it) },
            onCreatePersona = { viewModel.startCreatingPersona() },
            onEditPersona = { viewModel.startEditingPersona(it) },
            onDeletePersona = { viewModel.deletePersona(it) },
            onSignOut = { viewModel.signOut() }
        )

        // Persona Editor Modal
        if (state.showPersonaEditor) {
            PersonaEditorModal(
                persona = state.editingPersona,
                onDismiss = { viewModel.dismissPersonaEditor() },
                onSave = { viewModel.savePersona() },
                onUpdatePersona = { viewModel.updateEditingPersona(it) }
            )
        }
    }
}

// ProfileSettingsContent.kt
@Composable
private fun ProfileSettingsContent(
    state: ProfileState,
    onUpdateName: (String) -> Unit,
    onLinkAccount: () -> Unit,
    onNotificationToggle: () -> Unit,
    onThemeToggle: () -> Unit,
    onSelectPersona: (String) -> Unit,
    onCreatePersona: () -> Unit,
    onEditPersona: (CustomPersona) -> Unit,
    onDeletePersona: (String) -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECE5DD))
            .then(
                if (state.preferences?.theme == Theme.DARK) {
                    Modifier.graphicsLayer {
                        // Invert colors for dark mode
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix().apply {
                                set(0, 0, -1f)
                                set(1, 1, -1f)
                                set(2, 2, -1f)
                            }
                        )
                    }
                } else Modifier
            )
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profile & Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Identity Section
            item {
                IdentitySection(
                    user = state.user,
                    userName = state.userName,
                    isEditingName = state.isEditingName,
                    tempName = state.tempName,
                    onStartEditName = { /* handled in viewmodel */ },
                    onSaveName = { onUpdateName(it) },
                    onLinkAccount = onLinkAccount
                )
            }

            // Care Team Section
            item {
                CareTeamSection(
                    personas = state.allPersonas,
                    activePersonaId = state.preferences?.activePersonaId ?: "",
                    onSelectPersona = onSelectPersona,
                    onCreatePersona = onCreatePersona,
                    onEditPersona = onEditPersona,
                    onDeletePersona = onDeletePersona
                )
            }

            // Settings Section
            item {
                SettingsSection(
                    preferences = state.preferences,
                    onNotificationToggle = onNotificationToggle,
                    onThemeToggle = onThemeToggle
                )
            }

            // Sign Out Button
            item {
                Button(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.Transparent)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.icon_sign_out),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign Out",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Footer
            item {
                Text(
                    text = "TheraVoice v1.0 • Built with Gemini 2.5",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}









