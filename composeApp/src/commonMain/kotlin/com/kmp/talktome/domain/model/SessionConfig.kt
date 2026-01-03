package com.kmp.talktome.domain.model

data class SessionConfig(
    val systemInstruction: String,
    val voiceName: AIVoice
)