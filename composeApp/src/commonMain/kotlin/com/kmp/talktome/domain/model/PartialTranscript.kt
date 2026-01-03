package com.kmp.talktome.domain.model

data class PartialTranscript(
    val role: TranscriptRole,
    val text: String
)