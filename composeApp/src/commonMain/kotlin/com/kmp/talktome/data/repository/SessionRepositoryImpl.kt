package com.kmp.talktome.data.repository

import com.kmp.talktome.data.firebase.FirestoreCollections
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.repository.SessionRepository
import com.kmp.talktome.domain.util.Result
import com.kmp.talktome.uploadByteArray
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.storage.Data
import dev.gitlive.firebase.storage.FirebaseStorage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "SessionRepositoryImpl"

class SessionRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : SessionRepository {

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
                        Napier.e(message = "Error parsing session: ${e.message}", tag= TAG)
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
                        Napier.e(message = "Error getting recent sessions: ${e.message}", tag = TAG)
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
            Napier.e(message = "Error getting session by ID: ${e.message}", tag = TAG)
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
            Napier.e(message = "Error saving session: ${e.message}", tag = TAG)
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
            Napier.e(message = "Error updating session: ${e.message}", tag = TAG)
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
            Napier.e(message = "Error deleting session: ${e.message}", tag = TAG)
            Result.Error(e)
        }
    }

    override suspend fun uploadAudio(
        sessionId: String,
        userId: String,
        audioData: ByteArray
    ): Result<String> {
        return try {
            val path = "audio/$userId/$sessionId.m4a"
            val ref = storage.reference.child(path)

            ref.uploadByteArray(audioData)

            Result.Success(path)
        } catch (e: Exception) {
            Napier.e(message = "Error uploading audio: ${e.message}", tag = TAG)
            Result.Error(e)
        }
    }


    override suspend fun getAudioUrl(path: String): Result<String> {
        return try {
            val url = storage.reference.child(path).getDownloadUrl()
            Result.Success(url)
        } catch (e: Exception) {
            Napier.e(message = "Error getting audio URL: ${e.message}", tag = TAG)
            Result.Error(e)
        }
    }

    override suspend fun deleteAudio(path: String): Result<Unit> {
        return try {
            storage.reference.child(path).delete()
            Result.Success(Unit)
        } catch (e: Exception) {
            Napier.e(message = "Error deleting audio: ${e.message}", tag = TAG)
            Result.Error(e)
        }
    }
}
