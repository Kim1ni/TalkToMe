package com.kmp.talktome.domain.model

import kotlin.time.Clock

/**
 * Custom therapist persona created by user or default
 */
data class CustomPersona(
    val id: String = "",
    val name: String,
    val description: String,
    val instructions: String,
    val voiceName: AIVoice = AIVoice.PUCK,
    val isDefault: Boolean = false,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
) {
    companion object {
        fun empty() = CustomPersona(
            id = "",
            name = "",
            description = "",
            instructions = "",
            voiceName = AIVoice.PUCK,
            isDefault = false
        )

        /**
         * Get default personas
         */
        fun getDefaults(): List<CustomPersona> = listOf(
            CustomPersona(
                id = "empathetic",
                name = "Gentle Listener",
                description = "Warm, compassionate, and validating.",
                instructions = "You are a warm, empathetic, and compassionate therapist. Your goal is to listen effectively, provide deep validation, and create a safe space. Focus on emotions and support. Keep responses concise and conversational.",
                voiceName = AIVoice.KORE,
                isDefault = true
            ),
            CustomPersona(
                id = "direct",
                name = "Accountability Coach",
                description = "Direct, honest, and solution-oriented.",
                instructions = "You are a \"tough love\" accountability partner and coach. You are direct, honest, and solution-oriented. Do not coddle the user. Challenge their excuses politely but firmly. Focus on action and results. Keep responses concise and energetic.",
                voiceName = AIVoice.FENRIR,
                isDefault = true
            ),
            CustomPersona(
                id = "cbt",
                name = "CBT Specialist",
                description = "Analytical, identifying cognitive distortions.",
                instructions = "You are a CBT (Cognitive Behavioral Therapy) specialist. Your goal is to help the user identify cognitive distortions (like catastrophizing or black-and-white thinking). Ask Socratic questions to challenge negative thoughts. Keep responses concise and analytical but supportive.",
                voiceName = AIVoice.PUCK,
                isDefault = true
            )
        )
    }
}
