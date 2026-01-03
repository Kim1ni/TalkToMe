package com.kmp.talktome.domain.live

import kotlinx.coroutines.flow.StateFlow

interface AudioRecorder {
    val volumeFlow: StateFlow<Float>

    suspend fun startRecording()
    suspend fun stopRecording(): ByteArray?
}

expect fun getAudioRecorder(): AudioRecorder