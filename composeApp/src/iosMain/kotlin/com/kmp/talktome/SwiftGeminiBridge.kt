package com.kmp.talktome

import com.kmp.talktome.domain.model.LiveSessionCallbacks
import com.kmp.talktome.domain.model.SessionConfig

interface SwiftGeminiBridge {

    suspend fun connect(config: SessionConfig, callbacks: LiveSessionCallbacks): Result<Unit>
    suspend fun disconnect(): Result<ByteArray?>

}