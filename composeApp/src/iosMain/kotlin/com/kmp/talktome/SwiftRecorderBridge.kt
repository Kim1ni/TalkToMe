package com.kmp.talktome

import kotlinx.coroutines.flow.StateFlow

interface SwiftRecorderBridge {

     val volumeFlow: StateFlow<Float>

     suspend fun startRecording()

     suspend fun stopRecording(): ByteArray?

}