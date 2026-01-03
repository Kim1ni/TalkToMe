package com.kmp.talktome.ui.features.history.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun BackgroundAmbience() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 0.3f }
    ) {
        // Top-left teal glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF99F6E4),
                    Color.Transparent
                ),
                center = Offset(size.width * -0.1f, size.height * -0.1f),
                radius = size.width * 0.5f
            ),
            center = Offset(size.width * -0.1f, size.height * -0.1f),
            radius = size.width * 0.5f
        )

        // Bottom-right blue glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFBAE6FD),
                    Color.Transparent
                ),
                center = Offset(size.width * 1.1f, size.height * 1.1f),
                radius = size.width * 0.5f
            ),
            center = Offset(size.width * 1.1f, size.height * 1.1f),
            radius = size.width * 0.5f
        )
    }
}