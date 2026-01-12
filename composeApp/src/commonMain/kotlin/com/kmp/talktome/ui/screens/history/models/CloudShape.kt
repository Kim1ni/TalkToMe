package com.kmp.talktome.ui.screens.history.models

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class CloudShape {
    ROUND,
    ORGANIC_1,
    ORGANIC_2,
    ORGANIC_3;

    companion object {
        fun getCloudStyle(sentimentScore: Int, index: Int): CloudStyle {
            val shapes = listOf(
                ROUND,
                ORGANIC_1,
                ORGANIC_2,
                ORGANIC_3
            )

            val shapeType = shapes[index % shapes.size]
            val scale = 0.9f + ((index % 3) * 0.1f)

            return when {
                sentimentScore >= 70 -> {
                    // Happy/Good: Bright, White/Blue-ish
                    CloudStyle(
                        shapeType = shapeType,
                        gradientBrush = Brush.linearGradient(
                            colors = listOf(Color.White, Color(0xFFEFF6FF))
                        ),
                        borderColor = Color(0xFFDBEAFE),
                        textColor = Color(0xFF1E3A8A),
                        shadowColor = Color(0xFFBFDBFE).copy(alpha = 0.5f),
                        scale = scale
                    )
                }

                sentimentScore >= 40 -> {
                    // Neutral: WhatsApp Tea/Green-ish
                    CloudStyle(
                        shapeType = shapeType,
                        gradientBrush = Brush.linearGradient(
                            colors = listOf(Color(0xFFDCF8C6), Color(0xFFE5E7EB))
                        ),
                        borderColor = Color(0xFFBBF7D0),
                        textColor = Color(0xFF134E4A),
                        shadowColor = Color(0xFF25D366).copy(alpha = 0.2f),
                        scale = scale
                    )
                }

                else -> {
                    // Sad/Low: Stormy, Gray
                    CloudStyle(
                        shapeType = shapeType,
                        gradientBrush = Brush.linearGradient(
                            colors = listOf(Color(0xFFE5E7EB), Color(0xFFD1D5DB))
                        ),
                        borderColor = Color(0xFFD1D5DB),
                        textColor = Color(0xFF374151),
                        shadowColor = Color(0xFF9CA3AF).copy(alpha = 0.4f),
                        scale = scale
                    )
                }
            }
        }
    }
}

