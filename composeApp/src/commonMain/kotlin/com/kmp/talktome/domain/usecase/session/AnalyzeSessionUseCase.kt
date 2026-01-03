package com.kmp.talktome.domain.usecase.session

import com.kmp.talktome.domain.live.GeminiAnalysisService
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.model.SessionAnalysis
import com.kmp.talktome.domain.repository.SessionRepository

class AnalyzeSessionUseCase(
    private val geminiAnalysisService: GeminiAnalysisService,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(session: Session): Result<SessionAnalysis> {
        return geminiAnalysisService.analyzeTranscript(session.transcript)
            .onSuccess { analysis ->
                // Update session with analysis
                val updatedSession = session.copy(analysis = analysis)
                sessionRepository.updateSession(updatedSession)
            }
    }
}
