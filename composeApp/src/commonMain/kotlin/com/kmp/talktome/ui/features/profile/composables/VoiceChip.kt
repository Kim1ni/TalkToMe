package com.kmp.talktome.ui.features.profile.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.AIVoice


@Composable
fun VoiceChip(
    voice: AIVoice,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF128C7E) else Color.White,
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) Color(0xFF128C7E) else Color(0xFFE0E0E0)
        ),
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Text(
            text = voice.toDisplayString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color(0xFF616161),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )
    }
}
