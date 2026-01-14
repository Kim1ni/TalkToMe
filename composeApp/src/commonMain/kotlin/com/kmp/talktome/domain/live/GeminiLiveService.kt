package com.kmp.talktome.domain.live

import com.kmp.talktome.domain.live.LiveSessionCallbacks
import com.kmp.talktome.domain.live.SessionConfig
import com.kmp.talktome.domain.live.AIVoice
import com.kmp.talktome.domain.util.Result

/**
 * Platform-specific Gemini Live API service
 * Handles real-time audio streaming and transcription
 */
interface GeminiLiveService {

    suspend fun connect(config: SessionConfig, callbacks: LiveSessionCallbacks): Result<Unit>

    suspend fun disconnect(): Result<ByteArray?>

}

data class SessionConfig(
    val systemInstruction: String,
    val voiceName: AIVoice
)


/**
 * Voice options for AI therapist
 */
enum class AIVoice {
    PUCK, CHARON, KORE, FENRIR, AOEDE;

    fun toDisplayString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

interface LiveSessionCallbacks {
    fun onOpen()
    fun onClose()
    fun onError(error: Exception)
    fun onMessage(text: String, isUser: Boolean, timestamp: Long)
    fun onPartialTranscript(text: String?, isUser: Boolean)
    fun onVolumeUpdate(volume: Float)
}
