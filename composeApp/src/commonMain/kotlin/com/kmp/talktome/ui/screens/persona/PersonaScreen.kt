package com.kmp.talktome.ui.screens.persona

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmp.talktome.ui.composables.BaseHeader
import com.kmp.talktome.ui.screens.profile.composables.CareTeamSection
import com.kmp.talktome.ui.screens.persona.composables.PersonaEditorModal
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_check

@Composable
fun PersonaScreen(
    viewModel: PersonaViewModel = koinViewModel<PersonaViewModel>(),
    onBackPressed: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BaseHeader(
                onBackPressed = onBackPressed,
                title = "Manage Therapists"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::startCreatingPersona,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    painter = painterResource(Res.drawable.icon_check),
                    contentDescription = "Add Therapist",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        CareTeamSection(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            personas = state.personas,
            onCreatePersona = viewModel::startCreatingPersona,
            onEditPersona = viewModel::startEditingPersona,
            onDeletePersona = viewModel::deletePersona
        )

        if (state.showPersonaEditor) {
            PersonaEditorModal(
                persona = state.editingPersona,
                onDismiss = viewModel::dismissPersonaEditor,
                onSave = viewModel::savePersona,
                onUpdatePersona = viewModel::updateEditingPersona
            )
        }
    }
}
