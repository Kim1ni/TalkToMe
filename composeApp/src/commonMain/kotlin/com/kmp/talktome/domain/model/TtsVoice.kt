package com.kmp.talktome.domain.model

/**
 * Represents a voice available for Text-to-Speech (TTS) on the user's device.
 * This is a platform-agnostic domain model.
 *
 * @param id A unique, stable identifier for the voice provided by the platform's TTS engine.
 * @param name A user-friendly display name for the voice (e.g., "English (United States) - Female").
 */
data class TtsVoice(
    val id: String,
    val name: String,
    val language: String //= name.substringAfter("(").substringBefore(")"),
)
