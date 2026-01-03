package com.kmp.talktome.data.repository

import com.kmp.talktome.data.firebase.FirestoreCollections
import com.kmp.talktome.domain.model.User
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.util.Result
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FirebaseAuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: Flow<User?> = auth.authStateChanged.map { firebaseUser ->
        firebaseUser?.toUser()
    }

    override val isUserSignedIn: Flow<Boolean> = auth.authStateChanged.map { it != null }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.credential(idToken, null)
            val result = auth.signInWithCredential(credential)

            result.user?.let { firebaseUser ->
                val user = firebaseUser.toUser()
                saveUserToFirestore(user)
                Result.Success(user)
            } ?: Result.Error(Exception("Sign in failed"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signInAnonymously(): Result<User> {
        return try {
            val result = auth.signInAnonymously()
            result.user?.let { firebaseUser ->
                val user = firebaseUser.toUser()
                saveUserToFirestore(user)
                Result.Success(user)
            } ?: Result.Error(Exception("Anonymous sign in failed"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun linkWithGoogle(idToken: String): Result<User> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.Error(Exception("No user signed in"))

            val credential = GoogleAuthProvider.credential(idToken, null)
            val result = currentUser.linkWithCredential(credential)

            result.user?.let { firebaseUser ->
                val user = firebaseUser.toUser()
                saveUserToFirestore(user, merge = true)
                Result.Success(user)
            } ?: Result.Error(Exception("Link failed"))
        } /*catch (e: FirebaseAuthenticationUserCollisionException) {
            Result.Error(Exception("This Google account is already in use. Please sign in with Google directly."))
        }*/ catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateDisplayName(name: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.Error(Exception("No user signed in"))

            currentUser.updateProfile(displayName = name)

            // Update in Firestore
            firestore
                .collection(FirestoreCollections.USERS)
                .document(currentUser.uid)
                .update(mapOf("displayName" to name))

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    private suspend fun saveUserToFirestore(user: User, merge: Boolean = false) {
        try {
            firestore
                .collection(FirestoreCollections.USERS)
                .document(user.uid)
                .set(data = user, merge = merge)
        } catch (e: Exception) {
            // Log error but don't fail the sign in
            println("Failed to save user to Firestore: ${e.message}")
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun FirebaseUser.toUser() = User(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoURL,
        isAnonymous = isAnonymous,
        createdAt = Clock.System.now().toEpochMilliseconds()
    )
}