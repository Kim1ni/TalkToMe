package com.kmp.talktome.domain.usecase.login

import com.kmp.talktome.domain.model.User
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.util.Result

class SignInAnonymouslyUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> {
        return authRepository.signInAnonymously()
    }
}