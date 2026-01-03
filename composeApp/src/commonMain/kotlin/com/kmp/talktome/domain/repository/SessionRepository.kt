package com.kmp.talktome.domain.repository

import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    fun getUserSessions(userId: String): Flow<List<Session>>

    fun getRecentSessions(userId: String, limit: Int): Flow<List<Session>>

    suspend fun getSessionById(sessionId: String): Result<Session>

    suspend fun saveSession(session: Session): Result<Unit>

    suspend fun updateSession(session: Session): Result<Unit>

    suspend fun deleteSession(sessionId: String): Result<Unit>

    suspend fun uploadAudio(sessionId: String, userId: String, audioData: ByteArray): Result<String>

    suspend fun getAudioUrl(path: String): Result<String>

    suspend fun deleteAudio(path: String): Result<Unit>

}