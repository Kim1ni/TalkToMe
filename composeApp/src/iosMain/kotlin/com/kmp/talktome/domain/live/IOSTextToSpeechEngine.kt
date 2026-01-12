package com.kmp.talktome.domain.live

import com.kmp.talktome.domain.model.TtsVoice
import platform.AVFAudio.*
import platform.darwin.NSObject

class IOSTextToSpeechEngine : TextToSpeechEngine {

    private val synthesizer = AVSpeechSynthesizer()
    private var delegate: AVSpeechSynthesizerDelegate? = null

    override suspend fun getAvailableVoices(): List<TtsVoice> {
        return AVSpeechSynthesisVoice.speechVoices()
            .mapNotNull { it as? AVSpeechSynthesisVoice }
            .map { voice ->
                TtsVoice(
                    id = voice.identifier,
                    name = voice.name,
                    language = voice.language,
                )
            }
    }

    override fun speak(
        text: String,
        voiceName: TtsVoice?,
        pitch: Float,
        rate: Float,
        onComplete: () -> Unit,
        onError: () -> Unit
    ) {
        val utterance = AVSpeechUtterance(string = text)
        
        // Find voice by identifier if provided
        voiceName?.id?.let { id ->
            utterance.voice = AVSpeechSynthesisVoice.voiceWithIdentifier(id)
        }
        
        utterance.pitchMultiplier = pitch
        utterance.rate = rate
        
        delegate = AVSpeechSynthesizerDelegate(onComplete)
        synthesizer.delegate = delegate
        
        synthesizer.speakUtterance(utterance)
    }

    override fun stop() {
        if (synthesizer.isSpeaking()) {
            synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
        }
    }

    override fun shutdown() {
        stop()
        delegate = null
        synthesizer.delegate = null
    }

    private class AVSpeechSynthesizerDelegate(
        private val onComplete: () -> Unit
    ) : NSObject(), AVSpeechSynthesizerDelegateProtocol {
        override fun speechSynthesizer(
            synthesizer: AVSpeechSynthesizer,
            didFinishSpeechUtterance: AVSpeechUtterance
        ) {
            onComplete()
        }
    }
}
