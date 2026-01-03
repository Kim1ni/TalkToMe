package com.kmp.talktome.domain.usecase.session

import com.kmp.talktome.domain.live.GeminiLiveService
import com.kmp.talktome.domain.model.LiveSessionCallbacks
import com.kmp.talktome.domain.model.SessionConfig

class StartSessionUseCase(
    private val geminiLiveService: GeminiLiveService
) {
    suspend operator fun invoke(
        config: SessionConfig,
        callbacks: LiveSessionCallbacks
    ): Result<Unit> {
        return geminiLiveService.connect(config, callbacks)
    }
}