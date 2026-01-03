package com.kmp.talktome.domain.model

data class MoodInsight(
    val topic: String,
    val avgScore: Int,
    val count: Int
)