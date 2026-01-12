package com.kmp.talktome

import com.kmp.talktome.domain.model.LiveSessionCallbacks
import com.kmp.talktome.domain.model.SessionConfig

interface SwiftGeminiBridge {

    suspend fun connect(config: SessionConfig, modelName: String, callbacks: LiveSessionCallbacks)
    suspend fun disconnect(): ByteArray?

}