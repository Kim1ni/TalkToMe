package com.kmp.talktome.domain.live

import com.kmp.talktome.domain.model.LiveSessionCallbacks
import com.kmp.talktome.domain.model.SessionConfig
import com.kmp.talktome.domain.util.Result

/**
 * Platform-specific Gemini Live API service
 * Handles real-time audio streaming and transcription
 */
interface GeminiLiveService {

    suspend fun connect(config: SessionConfig, callbacks: LiveSessionCallbacks): Result<Unit>

    suspend fun disconnect(): Result<ByteArray?>

}
