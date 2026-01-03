package com.kmp.talktome.data.repository

import com.kmp.talktome.data.firebase.FirestoreCollections
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.repository.SessionRepository
import com.kmp.talktome.domain.util.Result
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import dev.gitlive.firebase.storage.Data as StorageData

class SessionRepositoryImpl : SessionRepository {
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage

    override fun getUserSessions(userId: String): Flow<List<Session>> {
        return firestore
            .collection(FirestoreCollections.SESSIONS)
            .where { "userId" equalTo userId }
            .orderBy("timestamp", Direction.DESCENDING)
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.data<Session>()
                    } catch (e: Exception) {
                        println("Error parsing session: ${e.message}")
                        null
                    }
                }
            }
    }

    override fun getRecentSessions(userId: String, limit: Int): Flow<List<Session>> {
        return firestore
            .collection(FirestoreCollections.SESSIONS)
            .where { "userId" equalTo userId }
            .orderBy("timestamp", Direction.DESCENDING)
            .limit(limit)
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.data<Session>()
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }

    override suspend fun getSessionById(sessionId: String): Result<Session> {
        return try {
            val document = firestore
                .collection(FirestoreCollections.SESSIONS)
                .document(sessionId)
                .get()

            val session = document.data<Session>()
            Result.Success(session)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveSession(session: Session): Result<Unit> {
        return try {
            firestore
                .collection(FirestoreCollections.SESSIONS)
                .document(session.id)
                .set(session)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateSession(session: Session): Result<Unit> {
        return try {
            firestore
                .collection(FirestoreCollections.SESSIONS)
                .document(session.id)
                .update(
                    mapOf("analysis" to session.analysis,
                    "audioStoragePath" to session.audioStoragePath)
                )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteSession(sessionId: String): Result<Unit> {
        return try {
            // Get session to find audio path
            val session = getSessionById(sessionId).getOrNull()

            // Delete audio if exists
            session?.audioStoragePath?.let { path ->
                deleteAudio(path)
            }

            // Delete session document
            firestore
                .collection(FirestoreCollections.SESSIONS)
                .document(sessionId)
                .delete()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun uploadAudio(
        sessionId: String,
        userId: String,
        audioData: ByteArray
    ): Result<String> {
        return try {
            val path = "audio/$userId/$sessionId.aac"
            val ref = storage.reference.child(path)

            ref.putData(audioData as Any as StorageData)

            Result.Success(path)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    override suspend fun getAudioUrl(path: String): Result<String> {
        return try {
            val url = storage.reference.child(path).getDownloadUrl()
            Result.Success(url)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteAudio(path: String): Result<Unit> {
        return try {
            storage.reference.child(path).delete()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
