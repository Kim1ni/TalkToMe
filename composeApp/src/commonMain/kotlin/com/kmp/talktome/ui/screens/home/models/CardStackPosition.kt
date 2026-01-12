package com.kmp.talktome.ui.screens.home.models

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Represents the position of a card within a stack.
 *
 * This enum is used to determine the appropriate corner rounding for a card
 * when it's displayed as part of a visual stack.
 * @property SINGLE Standalone card (all corners rounded)
 * @property TOP First card in stack (top corners rounded)
 * @property MIDDLE Middle card in stack (no corners rounded)
 * @property BOTTOM Last card in stack (bottom corners rounded)
 */
enum class CardStackPosition {
    SINGLE,
    TOP,
    MIDDLE,
    BOTTOM;

    fun getShape(cornerRadius: Dp = 16.dp): Shape {
        return when (this) {
            SINGLE -> RoundedCornerShape(cornerRadius)
            TOP -> RoundedCornerShape(
                topStart = cornerRadius,
                topEnd = cornerRadius,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            )
            MIDDLE -> RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 4.dp,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            )
            BOTTOM -> RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 4.dp,
                bottomStart = cornerRadius,
                bottomEnd = cornerRadius
            )
        }
    }
}