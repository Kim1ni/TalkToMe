package com.kmp.talktome.domain.usecase.session

import com.kmp.talktome.domain.live.GeminiLiveService

class EndSessionUseCase(
    private val geminiLiveService: GeminiLiveService
) {
    suspend operator fun invoke(): Result<ByteArray?> {
        return geminiLiveService.disconnect()
    }
}