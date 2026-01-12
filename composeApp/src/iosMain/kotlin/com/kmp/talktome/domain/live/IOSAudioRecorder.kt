package com.kmp.talktome.domain.live

import com.kmp.talktome.SwiftRecorderBridge
import kotlinx.coroutines.flow.StateFlow

class IOSAudioRecorder : AudioRecorder {

    var bridge: SwiftRecorderBridge? = null

    override val volumeFlow: StateFlow<Float>
        get() = TODO("Not yet implemented")

    override suspend fun startRecording() {
        bridge?.startRecording()
    }

    override suspend fun stopRecording(): ByteArray? {
        return bridge?.stopRecording()
    }

}
