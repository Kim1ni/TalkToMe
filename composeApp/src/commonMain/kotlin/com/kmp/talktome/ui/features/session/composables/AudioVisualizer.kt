
/*
package com.kmp.talktome.ui.features.session.composables

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.SessionStatus
import org.jetbrains.compose.resources.painterResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_mic

@Composable
fun AudioVisualizer(
    status: SessionStatus,
    volume: Float
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (status == SessionStatus.ACTIVE) {
            // Suggestion 1: Smoothly animate the scale based on volume changes.
            // This creates a direct, reactive "breathing" effect.
            val volumeScale by animateFloatAsState(
                targetValue = 1f + (volume * 1.5f), // Scale up to 2.5x based on volume
                animationSpec = tween(durationMillis = 150),
                label = "volumeScale"
            )

            // Suggestion 2: Add a subtle, continuous background pulse for life.
            val pulseScale by rememberInfiniteTransition(label = "pulse").animateFloat(
                initialValue = 1f,
                targetValue = 1.05f, // A very small, gentle pulse
                animationSpec = infiniteRepeatable(
                    animation = tween(1500),
                    repeatMode = RepeatMode.Reverse
                )
            )

            // Suggestion 3: Use a gradient for a softer, more modern look.
            val ringBrush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF128C7E).copy(alpha = 0.3f), // Opaque center
                    Color.Transparent // Fades out at the edge
                )
            )

            // Combined modifier for the rings
            val ringModifier = Modifier
                .fillMaxSize(0.8f) // Use a fraction of the screen for responsiveness
                .scale(volumeScale * pulseScale) // Combine both scales for a dynamic effect
                .background(
                    brush = ringBrush,
                    shape = CircleShape
                )

            // Render the rings
            Box(modifier = ringModifier)
        }

        // Center icon (unchanged, but now appears on top of the visualizer)
        Surface(
            modifier = Modifier.size(96.dp),
            shape = CircleShape,
            color = Color(0xFF128C7E),
            shadowElevation = 12.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (status == SessionStatus.CONNECTING) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.icon_mic),
                        contentDescription = "Microphone Icon",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
*/
package com.kmp.talktome.ui.features.session.composables

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.SessionStatus
import org.jetbrains.compose.resources.painterResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_mic


@Composable
fun AudioVisualizer(
    status: SessionStatus,
    volume: Float
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (status == SessionStatus.ACTIVE) {
            val scale by rememberInfiniteTransition().animateFloat(
                initialValue = 1f,
                targetValue = 1f + (volume * 2f),
                animationSpec = infiniteRepeatable(
                    animation = tween(100),
                    repeatMode = RepeatMode.Reverse
                )
            )

            // Outer ring
            Box(
                modifier = Modifier
                    .size((200 + volume * 500).dp)
                    .scale(scale)
                    .background(
                        color = Color(0xFF128C7E).copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            )

            // Inner ring
            Box(
                modifier = Modifier
                    .size((150 + volume * 300).dp)
                    .scale(scale)
                    .background(
                        color = Color(0xFF128C7E).copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )
        }

        // Center icon
        Surface(
            modifier = Modifier.size(96.dp),
            shape = CircleShape,
            color = Color(0xFF128C7E),
            shadowElevation = 12.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (status == SessionStatus.CONNECTING) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.icon_mic),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}