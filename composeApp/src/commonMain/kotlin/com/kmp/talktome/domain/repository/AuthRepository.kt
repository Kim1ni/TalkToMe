package com.kmp.talktome.domain.repository

import com.kmp.talktome.domain.model.User
import com.kmp.talktome.domain.util.Result
import kotlinx.coroutines.flow.Flow


interface AuthRepository {

    val currentUser: Flow<User?>

    val isUserSignedIn: Flow<Boolean>

    suspend fun signInWithGoogle(idToken: String): Result<User>

    suspend fun signInAnonymously(): Result<User>

    suspend fun linkWithGoogle(idToken: String): Result<User>

    suspend fun updateDisplayName(name: String): Result<Unit>

    suspend fun signOut()

    fun getCurrentUserId(): String?

}