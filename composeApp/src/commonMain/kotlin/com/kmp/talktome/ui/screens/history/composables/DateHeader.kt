package com.kmp.talktome.ui.screens.history.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DateHeader(
    date: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(50.dp),
                    // Use primary for a subtle themed glow in the shadow
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                )
                .background(
                    // Using a gradient blend between two container colors
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(50.dp)
                )
                .border(
                    width = 1.dp,
                    // Use outlineVariant for a professional, thin border
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(50.dp)
                )
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    // onSecondaryContainer ensures it's readable against the gradient
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}
