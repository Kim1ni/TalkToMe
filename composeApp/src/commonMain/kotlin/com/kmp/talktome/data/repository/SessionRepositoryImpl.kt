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
            val wavAudioData = addWavHeader(audioData)

            val path = "audio/$userId/$sessionId.wav"
            val ref = storage.reference.child(path)

            ref.uploadByteArray(wavAudioData)

            Result.Success(path)
        } catch (e: Exception) {
            Napier.e(message = "Error uploading audio: ${e.message}", tag = TAG)
            Result.Error(e)
        }
    }

    fun addWavHeader(pcmData: ByteArray): ByteArray {
        val sampleRate = 24000
        val channels = 1
        val bitDepth = 16
        val headerSize = 44
        val totalDataLen = pcmData.size
        val totalAudioLen = totalDataLen + headerSize - 8
        val byteRate = sampleRate * channels * bitDepth / 8

        val header = ByteArray(headerSize)
        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] =
            'F'.code.toByte()
        header[4] = (totalAudioLen and 0xff).toByte()
        header[5] = (totalAudioLen shr 8 and 0xff).toByte()
        header[6] = (totalAudioLen shr 16 and 0xff).toByte()
        header[7] = (totalAudioLen shr 24 and 0xff).toByte()
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] =
            'V'.code.toByte(); header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] =
            't'.code.toByte(); header[15] = ' '.code.toByte()
        header[16] = 16 // Subchunk1Size
        header[17] = 0; header[18] = 0; header[19] = 0
        header[20] = 1 // AudioFormat (PCM = 1)
        header[21] = 0
        header[22] = channels.toByte(); header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (0 and 0xff).toByte()
        header[27] = (0 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (0 and 0xff).toByte()
        header[31] = (0 and 0xff).toByte()
        header[32] = (channels * bitDepth / 8).toByte() // BlockAlign
        header[33] = 0
        header[34] = bitDepth.toByte(); header[35] = 0
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] =
            't'.code.toByte(); header[39] = 'a'.code.toByte()
        header[40] = (totalDataLen and 0xff).toByte()
        header[41] = (totalDataLen shr 8 and 0xff).toByte()
        header[42] = (totalDataLen shr 16 and 0xff).toByte()
        header[43] = (totalDataLen shr 24 and 0xff).toByte()

        return header + pcmData
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
