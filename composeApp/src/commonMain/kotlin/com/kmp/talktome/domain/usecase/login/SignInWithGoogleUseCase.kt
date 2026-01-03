package com.kmp.talktome.domain.usecase.login

import com.kmp.talktome.domain.model.User
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.util.Result

class SignInWithGoogleUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> {
        return authRepository.signInWithGoogle(idToken)
    }
}