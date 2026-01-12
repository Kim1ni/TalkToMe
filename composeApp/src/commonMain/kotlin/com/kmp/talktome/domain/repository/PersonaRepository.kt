package com.kmp.talktome.domain.repository

import com.kmp.talktome.domain.model.CustomPersona
import com.kmp.talktome.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface PersonaRepository {
    fun getPersonas(userId: String): Flow<List<CustomPersona>>
    suspend fun addPersona(userId: String, persona: CustomPersona): Result<Unit>
    suspend fun updatePersona(userId: String, persona: CustomPersona): Result<Unit>
    suspend fun deletePersona(userId: String, personaId: String): Result<Unit>
}
