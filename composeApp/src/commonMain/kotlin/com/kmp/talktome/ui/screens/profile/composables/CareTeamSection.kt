package com.kmp.talktome.ui.screens.profile.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.CustomPersona
import com.kmp.talktome.ui.composables.BaseHeader
import com.kmp.talktome.ui.screens.persona.composables.PersonaCard
import org.jetbrains.compose.resources.stringResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.profile_care_team

@Composable
fun CareTeamSection(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    personas: List<CustomPersona>,
    onCreatePersona: () -> Unit,
    onEditPersona: (CustomPersona) -> Unit,
    onDeletePersona: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = personas, key = { it.id }) { persona ->
            PersonaCard(
                persona = persona,
                onEdit = { onEditPersona(persona) },
                onDelete = { onDeletePersona(persona.id) }
            )
        }
    }
}