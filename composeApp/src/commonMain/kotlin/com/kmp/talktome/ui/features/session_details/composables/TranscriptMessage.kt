package com.kmp.talktome.ui.features.session_details.composables

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.TranscriptMessage
import com.kmp.talktome.domain.model.TranscriptRole
import kotlin.math.sin
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// TranscriptMessage.kt
@OptIn(ExperimentalTime::class)
@Composable
fun TranscriptMessage(
    message: TranscriptMessage,
    isActive: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.7f,
        animationSpec = tween(300)
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.role == TranscriptRole.USER)
            Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                },
            shape = when (message.role) {
                TranscriptRole.USER -> RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 4.dp
                )
                TranscriptRole.MODEL -> RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            },
            color = when (message.role) {
                TranscriptRole.USER -> Color(0xFFDCF8C6)
                TranscriptRole.MODEL -> Color.White
            },
            shadowElevation = if (isActive) 4.dp else 1.dp,
            border = if (message.role == TranscriptRole.MODEL) {
                BorderStroke(1.dp, Color(0xFFF5F5F5))
            } else null
        ) {
            Box {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF212121),
                    modifier = Modifier.padding(16.dp)
                )

                // Active glow effect
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .border(
                                width = 2.dp,
                                color = Color(0xFF128C7E).copy(alpha = 0.3f),
                                shape = when (message.role) {
                                    TranscriptRole.USER -> RoundedCornerShape(
                                        topStart = 16.dp, topEnd = 16.dp,
                                        bottomStart = 16.dp, bottomEnd = 4.dp
                                    )
                                    TranscriptRole.MODEL -> RoundedCornerShape(
                                        topStart = 4.dp, topEnd = 16.dp,
                                        bottomStart = 16.dp, bottomEnd = 16.dp
                                    )
                                }
                            )
                            .drawBehind {
                                val time = Clock.System.now().toEpochMilliseconds()/ 500f
                                val alpha = 0.2f + 0.2f * sin(time)
                                drawRect(
                                    color = Color(0xFF128C7E).copy(alpha = alpha),
                                    blendMode = BlendMode.Screen
                                )
                            }
                    )
                }
            }
        }
    }
}