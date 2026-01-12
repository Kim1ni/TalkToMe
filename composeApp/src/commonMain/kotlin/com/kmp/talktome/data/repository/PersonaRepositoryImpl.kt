package com.kmp.talktome.data.repository

import com.kmp.talktome.data.firebase.FirestoreCollections
import com.kmp.talktome.domain.model.CustomPersona
import com.kmp.talktome.domain.model.UserPreferences
import com.kmp.talktome.domain.repository.PersonaRepository
import com.kmp.talktome.domain.util.Result
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "PersonaRepository"

class PersonaRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PersonaRepository {

    override fun getPersonas(userId: String): Flow<List<CustomPersona>> {
        return firestore
            .collection(FirestoreCollections.PREFERENCES)
            .document(userId)
            .snapshots
            .map { snapshot ->
                try {
                    if (snapshot.exists) {
                        val prefs = snapshot.data<UserPreferences>()
                        CustomPersona.getDefaults() + prefs.customPersonas
                    } else {
                        CustomPersona.getDefaults()
                    }
                } catch (e: Exception) {
                    Napier.e(tag = TAG, message = "Error parsing personas: ${e.message}")
                    CustomPersona.getDefaults()
                }
            }
    }

    override suspend fun addPersona(userId: String, persona: CustomPersona): Result<Unit> {
        return try {
            val docRef = firestore
                .collection(FirestoreCollections.PREFERENCES)
                .document(userId)
            
            val doc = docRef.get()
            val currentPrefs = if (doc.exists) {
                doc.data<UserPreferences>()
            } else {
                UserPreferences(userId = userId)
            }

            val updatedPersonas = currentPrefs.customPersonas + persona
            val updates = mapOf("customPersonas" to updatedPersonas)
            
            if (doc.exists) {
                docRef.update(updates)
            } else {
                docRef.set(currentPrefs.copy(customPersonas = updatedPersonas))
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updatePersona(userId: String, persona: CustomPersona): Result<Unit> {
        return try {
            val docRef = firestore
                .collection(FirestoreCollections.PREFERENCES)
                .document(userId)

            val doc = docRef.get()
            val currentPrefs = if (doc.exists) {
                doc.data<UserPreferences>()
            } else {
                UserPreferences(userId = userId)
            }

            val updatedPersonas = currentPrefs.customPersonas.map {
                if (it.id == persona.id) persona else it
            }
            
            val updates = mapOf("customPersonas" to updatedPersonas)
            
            if (doc.exists) {
                docRef.update(updates)
            } else {
                docRef.set(currentPrefs.copy(customPersonas = updatedPersonas))
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deletePersona(userId: String, personaId: String): Result<Unit> {
        return try {
            val docRef = firestore
                .collection(FirestoreCollections.PREFERENCES)
                .document(userId)

            val doc = docRef.get()
            val currentPrefs = if (doc.exists) {
                doc.data<UserPreferences>()
            } else {
                UserPreferences(userId = userId)
            }

            val updatedPersonas = currentPrefs.customPersonas.filter { it.id != personaId }
            val updates = mutableMapOf<String, Any>("customPersonas" to updatedPersonas)

            if (currentPrefs.activePersonaId == personaId) {
                updates["activePersonaId"] = "empathetic"
            }

            if (doc.exists) {
                docRef.update(updates)
            } else {
                docRef.set(currentPrefs.copy(
                    customPersonas = updatedPersonas,
                    activePersonaId = updates["activePersonaId"] as? String ?: currentPrefs.activePersonaId
                ))
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
