package com.kmp.talktome.ui.screens.session.composables

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.SessionStatus
import com.kmp.talktome.ui.screens.session.models.BadgeProperties
import com.kmp.talktome.ui.theme.TalkToMeTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_error
import talktome.composeapp.generated.resources.session_status_connecting
import talktome.composeapp.generated.resources.session_status_ending
import talktome.composeapp.generated.resources.session_status_error
import talktome.composeapp.generated.resources.session_status_idle
import talktome.composeapp.generated.resources.session_status_live

@Composable
fun StatusBadge(status: SessionStatus, errorMessage: String?) {
    val (backgroundColor, textColor, text, icon) = when (status) {
        SessionStatus.ERROR ->
            BadgeProperties(
                backgroundColor = Color(0xFFFFEBEE),
                textColor = Color(0xFFD32F2F),
                text = stringResource( Res.string.session_status_error, errorMessage as Any),
                icon = Res.drawable.icon_error
            )
        SessionStatus.ACTIVE ->
            BadgeProperties(
                backgroundColor = Color(0xFFFFEBEE),
                textColor = Color(0xFFD32F2F),
                text = stringResource( Res.string.session_status_live),
                icon = null
            )
        SessionStatus.CONNECTING ->
            BadgeProperties(
                backgroundColor = Color(0xFFF5F5F5),
                textColor = Color(0xFF757575),
                text = stringResource( Res.string.session_status_connecting),
                icon = null)
        SessionStatus.ENDING ->
            BadgeProperties(
                backgroundColor = Color(0xFFFFF3E0),
                textColor = Color(0xFFF57C00),
                text = stringResource( Res.string.session_status_ending),
                icon = null
            )
        SessionStatus.ANALYZING ->
            BadgeProperties(
                backgroundColor = Color(0xFFE0FFE3),
                textColor = Color(0xFF2DF500),
                text = "Analysing",
                icon = null
            )
        else ->
            BadgeProperties(
                backgroundColor = Color(0xFFF5F5F5),
                textColor = Color(0xFF757575),
                text = stringResource( Res.string.session_status_idle),
                icon = null
            )
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            } else if (status == SessionStatus.ACTIVE) {
                // Pulsing dot
                val infiniteTransition = rememberInfiniteTransition()
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(textColor.copy(alpha = alpha), CircleShape)
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Preview
@Composable
fun StatusBadgePreview() {
    TalkToMeTheme {
        StatusBadge(
            status = SessionStatus.ERROR,
            errorMessage = "What the hell!!!"
        )
    }
}