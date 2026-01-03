package com.kmp.talktome.ui.features.session_details.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.talktome.domain.util.toFormattedTime
import com.kmp.talktome.ui.features.session_details.SessionDetailsState
import com.kmp.talktome.ui.features.session_details.models.PlaybackMode
import org.jetbrains.compose.resources.painterResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_pause
import talktome.composeapp.generated.resources.icon_play
import talktome.composeapp.generated.resources.icon_shield

// AudioPlayerBar.kt
@Composable
fun AudioPlayerBar(
    state: SessionDetailsState,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top Row - Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info Section
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Settings Button
                    Surface(
                        shape = CircleShape,
                        color = if (state.showSettings)
                            Color(0xFF128C7E) else Color(0xFFF5F5F5),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = onToggleSettings) {
                            Icon(
                                painter = painterResource(Res.drawable.icon_shield),
                                contentDescription = "Settings",
                                tint = if (state.showSettings)
                                    Color.White else Color(0xFF9E9E9E),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Mode Info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (state.playbackMode) {
                                PlaybackMode.RECORDING -> "Cloud Recording"
                                PlaybackMode.TTS -> "Transcript Reader"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = when (state.playbackMode) {
                                PlaybackMode.RECORDING -> when {
                                    state.isLoadingAudio -> "Syncing..."
                                    state.audioError -> "Unavailable"
                                    else -> "Original Audio"
                                }
                                PlaybackMode.TTS -> state.selectedVoice?.name
                                    ?.split(" ")?.firstOrNull() ?: "Default"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF757575),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Play Button
                Surface(
                    shape = CircleShape,
                    color = if (state.audioError && state.playbackMode == PlaybackMode.RECORDING)
                        Color(0xFF9E9E9E) else Color(0xFF128C7E),
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(48.dp)
                ) {
                    IconButton(
                        onClick = onTogglePlay,
                        enabled = !(state.audioError && state.playbackMode == PlaybackMode.RECORDING)
                    ) {
                        Icon(
                            painter = if (state.isPlaying)
                                painterResource(Res.drawable.icon_pause) else painterResource(Res.drawable.icon_play),
                            contentDescription = if (state.isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Progress Bar (Recording Mode)
            if (state.playbackMode == PlaybackMode.RECORDING && !state.audioError) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.currentTime.toLong().toFormattedTime(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF757575),
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.End
                    )

                    Slider(
                        value = state.currentTime,
                        onValueChange = onSeek,
                        valueRange = 0f..state.duration.coerceAtLeast(1f),
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF128C7E),
                            activeTrackColor = Color(0xFF128C7E),
                            inactiveTrackColor = Color(0xFFE0E0E0)
                        )
                    )

                    Text(
                        text = state.duration.toLong().toFormattedTime(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF757575),
                        modifier = Modifier.width(32.dp)
                    )
                }
            }

            // TTS Progress Indicator
            if (state.playbackMode == PlaybackMode.TTS) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reading Message ${state.ttsCurrentIndex + 1} of ${state.transcriptLength}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9E9E9E),
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
