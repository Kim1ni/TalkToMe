package com.kmp.talktome.domain.live

import com.kmp.talktome.SwiftTtsEngineBridge
import com.kmp.talktome.domain.model.TtsVoice

class IOSTextToSpeechEngine : TextToSpeechEngine {

    var bridge: SwiftTtsEngineBridge? = null

    override suspend fun getAvailableVoices(): List<TtsVoice> {
        return bridge?.getAvailableVoices() ?: emptyList()
    }

    override fun speak(
        text: String,
        voiceName: TtsVoice?,
        pitch: Float,
        rate: Float,
        onComplete: () -> Unit,
        onError: () -> Unit
    ) {
        bridge?.speak(text, voiceName, pitch, rate, onComplete, onError)
    }

    override fun stop() {
        bridge?.stop()
    }

    override fun shutdown() {
        bridge?.shutdown()
    }

}

actual fun getTextToSpeechEngine(): TextToSpeechEngine = IOSTextToSpeechEngine()