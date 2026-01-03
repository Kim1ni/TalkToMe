package com.kmp.talktome.domain.usecase.session

import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.model.TranscriptMessage
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.repository.SessionRepository
import com.kmp.talktome.domain.util.Result
import com.kmp.talktome.domain.util.map
import kotlin.time.Clock

class SaveSessionUseCase(
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        transcript: List<TranscriptMessage>,
        durationSeconds: Int,
        audioData: ByteArray?
    ): Result<Session> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.Error(Exception("User not authenticated"))

        val sessionId = generateSessionId()

        // Upload audio if available
        val audioPath = audioData?.let { data ->
            sessionRepository.uploadAudio(sessionId, userId, data)
                .getOrNull()
        }

        // Create session
        val session = Session(
            id = sessionId,
            userId = userId,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            durationSeconds = durationSeconds,
            transcript = transcript,
            audioStoragePath = audioPath
        )
/*
        return when (val saveResult = sessionRepository.saveSession(session)) {
            is Result.Success -> Result.Success(session)
            is Result.Error -> Result.Error(saveResult.exceptionOrNull() ?: Exception("Unknown error"))
            is Result.Loading -> Result.Loading
        }*/

        // Save to repository
        return sessionRepository.saveSession(session)
            .map { session }
    }

    private fun generateSessionId(): String {
        return "session_${Clock.System.now().toEpochMilliseconds()}_${(0..9999).random()}"
    }
}