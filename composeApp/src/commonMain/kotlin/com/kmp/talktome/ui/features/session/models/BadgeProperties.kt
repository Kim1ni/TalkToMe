package com.kmp.talktome.ui.features.session.models

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

data class BadgeProperties(
    val backgroundColor: Color,
    val textColor: Color,
    val text: String,
    val icon: DrawableResource?
)
