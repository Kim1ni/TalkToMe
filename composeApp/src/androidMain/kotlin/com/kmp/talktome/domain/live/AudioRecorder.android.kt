package com.kmp.talktome.domain.live

import android.Manifest
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

class AndroidAudioRecorder : AudioRecorder {


    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    private val outputDir = File("audio_recordings").apply { mkdirs() }
    private var audioStream: ByteArrayOutputStream? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var volumeJob: Job? = null

    private val _volumeFlow = MutableStateFlow(0f)
    override val volumeFlow: StateFlow<Float> = _volumeFlow.asStateFlow()

    private fun createRecorder(): MediaRecorder {
        // Use the modern MediaRecorder builder for API 31+
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder().apply {
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
        // Release any previous recorder
        recorder?.release()
        audioStream?.close()

        // Create a temporary file to record to
        outputFile = File.createTempFile("audio", ".mp4", outputDir)
        audioStream = ByteArrayOutputStream()

        recorder = createRecorder().apply {
            setOutputFile(outputFile?.absolutePath)
            try {
                prepare()
                start()
                startVolumeUpdates()
            } catch (e: Exception) {
                // Handle exceptions, e.g., permission denied or prepare failed
                println("AudioRecorder prepare failed: ${e.message}")
            }
        }
    }

    override suspend fun stopRecording(): ByteArray? {
        // Stop volume updates and recording
        volumeJob?.cancel()
        _volumeFlow.value = 0f

        try {
            recorder?.stop()
        } catch (e: Exception) {
            // It's common for stop() to throw if start() failed. We can ignore it
            // if we are cleaning up.
            println("AudioRecorder stop failed: ${e.message}")
            return null // Return null if stopping failed
        } finally {
            recorder?.release()
            recorder = null
        }

        // Read the temporary file into a byte array
        val recordedBytes = outputFile?.readBytes()
        outputFile?.delete() // Clean up the temporary file
        outputFile = null

        return recordedBytes
    }

    private fun startVolumeUpdates() {
        volumeJob?.cancel()
        volumeJob = scope.launch {
            while (true) {
                val maxAmplitude = recorder?.maxAmplitude ?: 0
                val volume = if (maxAmplitude > 0) {
                    // Normalize the amplitude to a 0-1 float scale
                    (20 * log10(maxAmplitude.toDouble())).toFloat() / 100
                } else {
                    0f
                }
                _volumeFlow.value = volume.coerceIn(0f, 1f)
                delay(100) // Update volume every 100ms
            }
        }
    }
}

// You will need to provide a context to create the recorder.
// This is typically done via dependency injection (like Koin).
// For now, let's assume you'll modify your DI module to provide the context's cacheDir.
// The `actual fun` needs to be updated to reflect this.

actual fun getAudioRecorder(): AudioRecorder = AndroidAudioRecorder()
