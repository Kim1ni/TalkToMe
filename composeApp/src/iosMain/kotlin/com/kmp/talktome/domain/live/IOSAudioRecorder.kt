package com.kmp.talktome.domain.live

import com.kmp.talktome.SwiftRecorderBridge
import kotlinx.coroutines.flow.StateFlow

class IOSAudioRecorder : AudioRecorder {

    var bridge: SwiftRecorderBridge? = null

    override val volumeFlow: StateFlow<Float>
        get() = bridge?.volumeFlow ?: throw IllegalStateException("Bridge not initialized")


    override suspend fun startRecording() {
        bridge?.startRecording()
    }

    override suspend fun stopRecording(): ByteArray? {
        return bridge?.stopRecording()
    }

}
