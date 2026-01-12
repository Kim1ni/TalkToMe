package com.kmp.talktome.domain.usecase.session

import com.kmp.talktome.domain.model.SessionAnalysis
import com.kmp.talktome.domain.repository.SessionRepository
import com.kmp.talktome.domain.util.Result
import io.github.aakira.napier.Napier

private const val TAG = "UpdateSessionAnalysisUseCase"

class UpdateSessionAnalysisUseCase(private val sessionRepository: SessionRepository) {

    suspend operator fun invoke(sessionId: String, analysis: SessionAnalysis): Result<Unit> {
        Napier.d("Attempting to update session analysis for sessionId: $sessionId", tag = TAG)

        // First, get the existing session
        val sessionResult = sessionRepository.getSessionById(sessionId)

        if (sessionResult is Result.Error) {
            Napier.e("Failed to get session with id: $sessionId", sessionResult.exception, tag = TAG)
            return Result.Error(sessionResult.exception)
        }

        val existingSession = (sessionResult as Result.Success).data
        Napier.d("Successfully retrieved session: $existingSession", tag = TAG)

        // Update the analysis
        val updatedSession = existingSession.copy(analysis = analysis)
        Napier.d("Updated session with new analysis: $updatedSession", tag = TAG)

        // Save the updated session
        val updateResult = sessionRepository.updateSession(updatedSession)

        if (updateResult is Result.Success) {
            Napier.d("Successfully updated session in repository", tag = TAG)
        } else if (updateResult is Result.Error) {
            Napier.e("Failed to update session in repository", updateResult.exception, tag = TAG)
        }

        return updateResult
    }
}