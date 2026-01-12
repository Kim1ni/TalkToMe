package com.kmp.talktome.domain.live

import com.kmp.talktome.domain.model.TtsVoice

interface TextToSpeechEngine {

    suspend fun getAvailableVoices(): List<TtsVoice>

    fun speak(
        text: String,
        voiceName: TtsVoice?,
        pitch: Float = 1.0f,
        rate: Float = 1.0f,
        onComplete: () -> Unit,
        onError: () -> Unit
    )

    fun stop()

    fun shutdown()

}
