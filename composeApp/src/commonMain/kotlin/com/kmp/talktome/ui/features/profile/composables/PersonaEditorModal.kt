package com.kmp.talktome.ui.features.profile.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.talktome.domain.model.AIVoice
import com.kmp.talktome.domain.model.CustomPersona
import org.jetbrains.compose.resources.painterResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_link
import talktome.composeapp.generated.resources.icon_trash

// PersonaEditorModal.kt
@Composable
fun PersonaEditorModal(
    persona: CustomPersona?,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onUpdatePersona: (CustomPersona) -> Unit
) {
    var editedPersona by remember(persona) {
        mutableStateOf(persona ?: CustomPersona.empty())
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFAFAFA),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (persona?.isDefault == true)
                            "View Details"
                        else
                            "Custom Therapist",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    Surface(
                        onClick = onDismiss,
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(// TODO Add close icon
                                painter = painterResource(Res.drawable.icon_trash),
                                contentDescription = "Close",
                                tint = Color(0xFF616161),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Form Content
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Name Field
                item {
                    PersonaTextField(
                        label = "INTERNAL NAME",
                        value = editedPersona.name,
                        onValueChange = { editedPersona = editedPersona.copy(name = it) },
                        placeholder = "e.g., Grandma Vibes",
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Description Field
                item {
                    Column {
                        Text(
                            text = "MENU DESCRIPTION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF757575),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "(What you see)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFBDBDBD)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editedPersona.description,
                            onValueChange = { editedPersona = editedPersona.copy(description = it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("e.g., Sweet, patient, and cookie-baking energy")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF128C7E),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Instructions Field
                item {
                    Column {
                        Text(
                            text = "AI INSTRUCTIONS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF757575),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "(How it acts)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFBDBDBD)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Info Banner
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE3F2FD)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(// Add info icon
                                    painter = painterResource(Res.drawable.icon_link),
                                    contentDescription = null,
                                    tint = Color(0xFF2196F3),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "This is the \"System Prompt\". Tell the AI exactly who to be. Example: \"You are a wise philosopher. Use metaphors about nature.\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF1976D2),
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editedPersona.instructions,
                            onValueChange = {
                                editedPersona = editedPersona.copy(instructions = it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            placeholder = { Text("You are a...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF128C7E),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 22.sp
                            ),
                            maxLines = Int.MAX_VALUE
                        )
                    }
                }

                // Voice Selection
                item {
                    Column {
                        Text(
                            text = "VOICE PREFERENCE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF757575),
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(AIVoice.entries.toTypedArray()) { voice ->
                                VoiceChip(
                                    voice = voice,
                                    isSelected = editedPersona.voiceName == voice,
                                    onClick = {
                                        editedPersona = editedPersona.copy(voiceName = voice)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Save Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFAFAFA),
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        onUpdatePersona(editedPersona)
                        onSave()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF128C7E)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text(
                        text = "Save Therapist",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}