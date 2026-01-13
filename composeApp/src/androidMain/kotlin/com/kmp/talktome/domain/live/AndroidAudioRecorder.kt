package com.kmp.talktome.domain.live

import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.log10

class AndroidAudioRecorder(
    private val context: Context
) : AudioRecorder {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    private val outputDir = File(context.cacheDir, "audio_recordings").apply { mkdirs() }
    private var audioStream: ByteArrayOutputStream? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var volumeJob: Job? = null

    private val _volumeFlow = MutableStateFlow(0f)
    override val volumeFlow: StateFlow<Float> = _volumeFlow.asStateFlow()

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            }
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            }
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override suspend fun startRecording() {
        recorder?.release()
        audioStream?.close()

        outputFile = File.createTempFile("audio", ".wav", outputDir)
        audioStream = ByteArrayOutputStream()

        recorder = createRecorder().apply {
            setOutputFile(outputFile?.absolutePath)
            try {
                prepare()
                start()
                startVolumeUpdates()
            } catch (e: Exception) {
                println("AudioRecorder prepare failed: ${e.message}")
            }
        }
    }

    override suspend fun stopRecording(): ByteArray? {
        volumeJob?.cancel()
        _volumeFlow.value = 0f

        try {
            recorder?.stop()
        } catch (e: Exception) {
            println("AudioRecorder stop failed: ${e.message}")
            return null
        } finally {
            recorder?.release()
            recorder = null
        }

        val recordedBytes = outputFile?.readBytes()
        outputFile?.delete()
        outputFile = null

        return recordedBytes
    }

    private fun startVolumeUpdates() {
        volumeJob?.cancel()
        volumeJob = scope.launch {
            while (true) {
                val maxAmplitude = recorder?.maxAmplitude ?: 0
                val volume = if (maxAmplitude > 0) {
                    (20 * log10(maxAmplitude.toDouble())).toFloat() / 100
                } else {
                    0f
                }
                _volumeFlow.value = volume.coerceIn(0f, 1f)
                delay(100)
            }
        }
    }
}
