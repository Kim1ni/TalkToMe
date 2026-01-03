package com.kmp.talktome.domain.live

import com.kmp.talktome.domain.model.SessionAnalysis
import com.kmp.talktome.domain.model.TranscriptMessage

/**
 * Gemini API for session analysis (non-realtime)
 */
interface GeminiAnalysisService {
    suspend fun analyzeTranscript(transcript: List<TranscriptMessage>): Result<SessionAnalysis>
}