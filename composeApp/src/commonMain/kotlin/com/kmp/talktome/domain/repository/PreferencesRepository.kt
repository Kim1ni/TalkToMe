package com.kmp.talktome.domain.repository

import com.kmp.talktome.domain.model.CustomPersona
import com.kmp.talktome.domain.model.UserPreferences
import com.kmp.talktome.domain.util.Result
import kotlinx.coroutines.flow.Flow


interface PreferencesRepository {
    fun getUserPreferences(userId: String): Flow<UserPreferences?>
    suspend fun savePreferences(preferences: UserPreferences): Result<Unit>
    suspend fun updatePreferences(userId: String, updates: Map<String, Any>): Result<Unit>
    suspend fun addCustomPersona(userId: String, persona: CustomPersona): Result<Unit>
    suspend fun updateCustomPersona(userId: String, persona: CustomPersona): Result<Unit>
    suspend fun deleteCustomPersona(userId: String, personaId: String): Result<Unit>
}