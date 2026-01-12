package com.kmp.talktome.domain.usecase.session

import com.kmp.talktome.domain.live.GeminiLiveService
import com.kmp.talktome.domain.util.Result

class EndSessionUseCase(
    private val geminiLiveService: GeminiLiveService
) {
    suspend operator fun invoke(): Result<ByteArray?> {
        return geminiLiveService.disconnect()
    }
}