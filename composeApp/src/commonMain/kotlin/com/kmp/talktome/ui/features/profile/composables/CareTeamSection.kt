package com.kmp.talktome.ui.features.profile.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.talktome.domain.model.CustomPersona
import org.jetbrains.compose.resources.painterResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_link

// CareTeamSection.kt
@Composable
fun CareTeamSection(
    personas: List<CustomPersona>,
    activePersonaId: String,
    onSelectPersona: (String) -> Unit,
    onCreatePersona: () -> Unit,
    onEditPersona: (CustomPersona) -> Unit,
    onDeletePersona: (String) -> Unit
) {
    Column {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MY CARE TEAM",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF757575),
                letterSpacing = 1.5.sp
            )
            TextButton(onClick = onCreatePersona) {
                Icon(// TODO Add add icon
                    painter = painterResource(Res.drawable.icon_link),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFF128C7E)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Create Custom",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF128C7E)
                )
            }
        }

        // Persona Cards
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            personas.forEach { persona ->
                PersonaCard(
                    persona = persona,
                    isActive = activePersonaId == persona.id,
                    onSelect = { onSelectPersona(persona.id) },
                    onEdit = { onEditPersona(persona) },
                    onDelete = { onDeletePersona(persona.id) }
                )
            }
        }
    }
}