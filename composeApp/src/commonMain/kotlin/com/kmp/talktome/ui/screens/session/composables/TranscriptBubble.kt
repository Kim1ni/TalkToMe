package com.kmp.talktome.ui.screens.session.composables

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
import com.kmp.talktome.ui.theme.TalkToMeTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun TranscriptBubble(
    message: TranscriptMessage,
    isPartial: Boolean = false
) {
    val alignment = if (message.role == TranscriptRole.USER)
        Alignment.TopEnd else Alignment.TopStart

    val backgroundColor = when (message.role) {
        TranscriptRole.USER -> MaterialTheme.colorScheme.primaryContainer
        TranscriptRole.MODEL -> MaterialTheme.colorScheme.secondaryContainer
    }

    val contentColor = when (message.role) {
        TranscriptRole.USER -> MaterialTheme.colorScheme.onPrimaryContainer
        TranscriptRole.MODEL -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    val shape = if (message.role == TranscriptRole.USER) {
        RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
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
            contentColor = contentColor,
            shadowElevation = if (isPartial) 0.dp else 1.dp
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
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
                            .background(contentColor.copy(alpha = alpha))
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TranscriptBubbleUserPreview() {
    TalkToMeTheme {
        TranscriptBubble(
            message = TranscriptMessage(
                role = TranscriptRole.USER,
                text = "Hello!!!"
            ),
            isPartial = true
        )

    }
}

@Preview
@Composable
fun TranscriptBubbleModelPreview() {
    TalkToMeTheme {
        TranscriptBubble(
            message = TranscriptMessage(
                role = TranscriptRole.MODEL,
                text = "Hello, how can I assist you today?"
            ),
            isPartial = true
        )
    }
}
