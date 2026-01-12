package com.kmp.talktome.domain.live

import com.kmp.talktome.BuildKonfig
import com.kmp.talktome.SwiftGeminiBridge
import com.kmp.talktome.domain.model.LiveSessionCallbacks
import com.kmp.talktome.domain.model.SessionConfig
import com.kmp.talktome.domain.util.Result

class IOSGeminiLiveService : GeminiLiveService {

    companion object {
        var bridge: SwiftGeminiBridge? = null
    }

    override suspend fun connect(
        config: SessionConfig,
        callbacks: LiveSessionCallbacks
    ): Result<Unit> {
        return try {
            bridge?.connect(config = config, modelName = BuildKonfig.LIVE_MODEL_NAME, callbacks = callbacks)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun disconnect(): Result<ByteArray?> {
        return try {
            val audio = bridge?.disconnect()
            Result.Success(audio)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
