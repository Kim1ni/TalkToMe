package com.kmp.talktome.ui.screens.session_details.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.talktome.domain.util.toFormattedTime
import com.kmp.talktome.ui.screens.session_details.SessionDetailsState
import com.kmp.talktome.ui.screens.session_details.models.PlaybackMode
import org.jetbrains.compose.resources.painterResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_adjustments
import talktome.composeapp.generated.resources.icon_pause
import talktome.composeapp.generated.resources.icon_play

@Composable
fun AudioPlayerBar(
    state: SessionDetailsState,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
                        // Toggle between primary and a neutral container color
                        color = if (state.showSettings)
                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = onToggleSettings) {
                            Icon(
                                painter = painterResource(Res.drawable.icon_adjustments),
                                contentDescription = "Settings",
                                // Contrast icon color based on background
                                tint = if (state.showSettings)
                                    MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
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
                            color = MaterialTheme.colorScheme.onSurface,
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Play Button
                Surface(
                    shape = CircleShape,
                    // Use error color if disabled/error, otherwise primary
                    color = if (state.audioError && state.playbackMode == PlaybackMode.RECORDING)
                        MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
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
                            tint = if (state.audioError && state.playbackMode == PlaybackMode.RECORDING)
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onPrimary,
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.End
                    )

                    Slider(
                        value = state.currentTime,
                        onValueChange = onSeek,
                        valueRange = 0f..state.duration.coerceAtLeast(1f),
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )

                    Text(
                        text = state.duration.toLong().toFormattedTime(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
