package com.kmp.talktome.ui.screens.session.models

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

data class BadgeProperties(
    val backgroundColor: Color,
    val textColor: Color,
    val text: String,
    val icon: DrawableResource?
)
