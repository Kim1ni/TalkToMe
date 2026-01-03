package com.kmp.talktome.ui.features.session.composables

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.TranscriptMessage
import com.kmp.talktome.domain.model.TranscriptRole


@Composable
fun TranscriptBubble(
    message: TranscriptMessage,
    isPartial: Boolean = false
) {
    val alignment = if (message.role == TranscriptRole.USER)
        Alignment.TopEnd else Alignment.TopStart

    val backgroundColor = when {
        isPartial && message.role == TranscriptRole.USER -> Color(0xFFE9FCD4)
        message.role == TranscriptRole.USER -> Color(0xFFDCF8C6)
        isPartial -> Color(0xFFFAFAFA)
        else -> Color.White
    }

    val shape = if (message.role == TranscriptRole.USER) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = shape,
            color = backgroundColor,
            shadowElevation = if (isPartial) 0.dp else 1.dp
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF212121),
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (isPartial) {
                    Spacer(modifier = Modifier.width(4.dp))
                    // Blinking cursor
                    val infiniteTransition = rememberInfiniteTransition()
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(width = 2.dp, height = 12.dp)
                            .background(Color(0xFF128C7E).copy(alpha = alpha))
                    )
                }
            }
        }
    }
}