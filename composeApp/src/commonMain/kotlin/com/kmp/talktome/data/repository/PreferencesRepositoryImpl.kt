package com.kmp.talktome.data.repository

import com.kmp.talktome.data.firebase.FirestoreCollections
import com.kmp.talktome.domain.model.CustomPersona
import com.kmp.talktome.domain.model.UserPreferences
import com.kmp.talktome.domain.repository.PreferencesRepository
import com.kmp.talktome.domain.util.Result
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepositoryImpl : PreferencesRepository {
    private val firestore = Firebase.firestore

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
                    println("Error parsing preferences: ${e.message}")
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
            Result.Error(e)
        }
    }

    override suspend fun updatePreferences(
        userId: String,
        updates: Map<String, Any>
    ): Result<Unit> {
        return try {
            firestore
                .collection(FirestoreCollections.PREFERENCES)
                .document(userId)
                .update(updates)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun addCustomPersona(
        userId: String,
        persona: CustomPersona
    ): Result<Unit> {
        return try {
            // Get current preferences
            val doc = firestore
                .collection(FirestoreCollections.PREFERENCES)
                .document(userId)
                .get()

            val currentPrefs = if (doc.exists) {
                doc.data<UserPreferences>()
            } else {
                UserPreferences(userId = userId)
            }

            // Add new persona
            val updatedPersonas = currentPrefs.customPersonas + persona

            // Save back
            firestore
                .collection(FirestoreCollections.PREFERENCES)
                .document(userId)
                .update(mapOf("customPersonas" to updatedPersonas))

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateCustomPersona(
        userId: String,
        persona: CustomPersona
    ): Result<Unit> {
        return try {
            val doc = firestore
                .collection(FirestoreCollections.PREFERENCES)
                .document(userId)
                .get()

            val currentPrefs = doc.data<UserPreferences>()

            // Update persona in list
            val updatedPersonas = currentPrefs.customPersonas.map {
                if (it.id == persona.id) persona else it
            }

            // Save back
            firestore
                .collection(FirestoreCollections.PREFERENCES)
                .document(userId)
                .update(mapOf("customPersonas" to updatedPersonas))

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteCustomPersona(
        userId: String,
        personaId: String
    ): Result<Unit> {
        return try {
            val doc = firestore
                .collection(FirestoreCollections.PREFERENCES)
                .document(userId)
                .get()

            val currentPrefs = doc.data<UserPreferences>()

            // Remove persona from list
            val updatedPersonas = currentPrefs.customPersonas
                .filter { it.id != personaId }

            // If deleted persona was active, reset to default
            val updates = mutableMapOf<String, Any>(
                "customPersonas" to updatedPersonas
            )

            if (currentPrefs.activePersonaId == personaId) {
                updates["activePersonaId"] = "empathetic"
            }

            // Save back
            firestore
                .collection(FirestoreCollections.PREFERENCES)
                .document(userId)
                .update(updates)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}