package com.kmp.talktome.domain.live

import com.kmp.talktome.SwiftGeminiBridge
import com.kmp.talktome.domain.model.LiveSessionCallbacks
import com.kmp.talktome.domain.model.SessionConfig

class IOSGeminiLiveService : GeminiLiveService {

    var bridge: SwiftGeminiBridge? = null

    override suspend fun connect(
        config: SessionConfig,
        callbacks: LiveSessionCallbacks
    ): Result<Unit> {
        bridge?.connect(config = config, callbacks = callbacks)
        return Result.success(Unit)
    }

    override suspend fun disconnect(): Result<ByteArray?> {
        bridge?.disconnect()
        return Result.success(null)
    }

}

actual fun getGeminiLiveService(): GeminiLiveService = IOSGeminiLiveService()