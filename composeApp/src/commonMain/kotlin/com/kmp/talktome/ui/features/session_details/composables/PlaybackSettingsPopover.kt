package com.kmp.talktome.ui.features.session_details.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.talktome.domain.model.TtsVoice
import com.kmp.talktome.ui.features.session_details.models.PlaybackMode

// PlaybackSettingsPopover.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackSettingsPopover(
    playbackMode: PlaybackMode,
    voices: List<TtsVoice>,
    selectedVoice: TtsVoice?,
    audioError: Boolean,
    onModeSwitch: (PlaybackMode) -> Unit,
    onVoiceSelected: (TtsVoice) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.width(256.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 16.dp,
        border = BorderStroke(1.dp, Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "PLAYBACK SETTINGS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9E9E9E),
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Mode Toggle
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF5F5F5)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp)
                ) {
                    ModeButton(
                        text = "Original",
                        isSelected = playbackMode == PlaybackMode.RECORDING,
                        onClick = { onModeSwitch(PlaybackMode.RECORDING) },
                        enabled = !audioError,
                        modifier = Modifier.weight(1f)
                    )
                    ModeButton(
                        text = "AI Reader",
                        isSelected = playbackMode == PlaybackMode.TTS,
                        onClick = { onModeSwitch(PlaybackMode.TTS) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Voice Selection (TTS Mode)
            if (playbackMode == PlaybackMode.TTS) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Narrator Voice",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF616161),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedVoice?.name?.take(30) ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF128C7E),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        textStyle = MaterialTheme.typography.bodySmall
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        voices.forEach { voice ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = voice.name.take(30),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                onClick = {
                                    onVoiceSelected(voice)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Error Message
            if (audioError && playbackMode == PlaybackMode.RECORDING) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recording unavailable. Using AI Reader.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFD32F2F)
                )
            }
        }
    }
}
