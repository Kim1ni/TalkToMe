package com.kmp.talktome.ui.features.profile.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.CustomPersona
import org.jetbrains.compose.resources.painterResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_link
import talktome.composeapp.generated.resources.icon_trash
import talktome.composeapp.generated.resources.icon_volume_up

// PersonaCard.kt
@Composable
fun PersonaCard(
    persona: CustomPersona,
    isActive: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(
            width = 2.dp,
            color = if (isActive) Color(0xFF128C7E) else Color.Transparent
        )
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = persona.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        if (isActive) {
                            Icon(// TODO check active icon
                                painter = painterResource(Res.drawable.icon_link),
                                contentDescription = "Active",
                                tint = Color(0xFF128C7E),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = persona.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF5F5F5)
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.icon_volume_up),
                                    contentDescription = null,
                                    tint = Color(0xFF616161),
                                    modifier = Modifier.size(12.dp)
                                )/* TODO Add voice icon
                                Text(
                                    text = persona.voiceName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF616161)
                                )*/
                            }
                        }

                        if (!persona.isDefault) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF3E5F5)
                            ) {
                                Text(
                                    text = "Custom",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF9C27B0),
                                    modifier = Modifier.padding(
                                        horizontal = 8.dp,
                                        vertical = 4.dp
                                    )
                                )
                            }
                        }
                    }
                }

                // Action Buttons (Custom personas only)
                if (!persona.isDefault) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(// TODO Add edit icon
                                painter = painterResource(Res.drawable.icon_link),
                                contentDescription = "Edit",
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.icon_trash),
                                contentDescription = "Delete",
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Active indicator background
            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 24.dp, y = (-24).dp)
                        .background(
                            color = Color(0xFF128C7E).copy(alpha = 0.05f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}
