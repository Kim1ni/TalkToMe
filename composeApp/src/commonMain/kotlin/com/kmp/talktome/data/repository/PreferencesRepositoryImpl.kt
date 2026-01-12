package com.kmp.talktome.data.repository

import com.kmp.talktome.data.firebase.FirestoreCollections
import com.kmp.talktome.domain.model.UserPreferences
import com.kmp.talktome.domain.repository.PreferencesRepository
import com.kmp.talktome.domain.util.Result
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private const val TAG = "PreferencesRepository"

class PreferencesRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PreferencesRepository {

    override fun getUserPreferences(userId: String): Flow<UserPreferences?> {
        return firestore
            .collection(FirestoreCollections.PREFERENCES)
            .document(userId)
            .snapshots
            .map { snapshot ->
                try {
                    if (snapshot.exists) {
                        snapshot.data<UserPreferences>()
                    } else {
                        // Return default preferences
                        UserPreferences(userId = userId)
                    }
                } catch (e: Exception) {
                    Napier.d(tag = TAG, message = "Error parsing preferences: ${e.message}")
                    UserPreferences(userId = userId)
                }
            }
    }

    override suspend fun savePreferences(preferences: UserPreferences): Result<Unit> {
        return try {
            firestore
                .collection(FirestoreCollections.PREFERENCES)
                .document(preferences.userId)
                .set(preferences)
            Result.Success(Unit)
        } catch (e: Exception) {
            Napier.e(message = "Error saving preferences: ${e.message}", tag = TAG)
            Result.Error(e)
        }
    }

    override suspend fun updatePreferences(
        userId: String,
        updates: Map<String, Any>
    ): Result<Unit> {
        Napier.d(tag = TAG, message = "[DEBUG_LOG] updatePreferences called for user $userId with updates: $updates")
        return try {
            val docRef = firestore
                .collection(FirestoreCollections.PREFERENCES)
                .document(userId)

            val doc = docRef.get()
            if (doc.exists) {
                docRef.update(updates)
            } else {
                // If document doesn't exist, create it with default preferences and the updates
                val defaultPrefs = UserPreferences(userId = userId)
                // We need to convert UserPreferences to a map and apply updates, 
                // but since it's a simple object we can just set it and then update, 
                // or better, create a merged map if possible.
                // For simplicity, let's set the defaults first then update.
                docRef.set(defaultPrefs)
                docRef.update(updates)
            }

            Napier.d(tag = TAG, message = "[DEBUG_LOG] updatePreferences success for user $userId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Napier.d(tag = TAG, message = "[DEBUG_LOG] updatePreferences error for user $userId: ${e.message}")
            Result.Error(e)
        }
    }
}