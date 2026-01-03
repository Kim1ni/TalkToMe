package com.kmp.talktome.ui.features.history.models

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class CloudStyle(
    val shapeType: CloudShape,
    val gradientBrush: Brush,
    val borderColor: Color,
    val textColor: Color,
    val shadowColor: Color,
    val scale: Float
)