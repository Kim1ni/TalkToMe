package com.kmp.talktome

import com.kmp.talktome.domain.model.TtsVoice

interface SwiftTtsEngineBridge {

     suspend fun getAvailableVoices(): List<TtsVoice>

     fun speak(
        text: String,
        voiceName: TtsVoice?,
        pitch: Float,
        rate: Float,
        onComplete: () -> Unit,
        onError: () -> Unit
    )

     fun stop()

     fun shutdown()
    
}