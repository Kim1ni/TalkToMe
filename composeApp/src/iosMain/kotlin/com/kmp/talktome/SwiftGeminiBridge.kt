package com.kmp.talktome

import com.kmp.talktome.domain.live.LiveSessionCallbacks
import com.kmp.talktome.domain.live.SessionConfig

interface SwiftGeminiBridge {

    suspend fun connect(config: SessionConfig, modelName: String, callbacks: LiveSessionCallbacks)
    suspend fun disconnect(): ByteArray?

}