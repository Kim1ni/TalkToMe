package com.kmp.talktome.domain.usecase.login

import com.kmp.talktome.domain.model.User
import com.kmp.talktome.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.currentUser
    }
}