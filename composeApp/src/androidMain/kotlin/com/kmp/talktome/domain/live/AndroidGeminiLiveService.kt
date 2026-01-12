@file: OptIn(PublicPreviewAPI::class)

package com.kmp.talktome.domain.live

import android.Manifest
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.AudioTranscriptionConfig
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.InlineData
import com.google.firebase.ai.type.InlineDataPart
import com.google.firebase.ai.type.LiveServerContent
import com.google.firebase.ai.type.LiveSession
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.SpeechConfig
import com.google.firebase.ai.type.Voice
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.liveGenerationConfig
import com.kmp.talktome.BuildKonfig
import com.kmp.talktome.domain.model.LiveSessionCallbacks
import com.kmp.talktome.domain.model.SessionConfig
import com.kmp.talktome.domain.util.Result
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TAG = "GeminiLiveService"

class AndroidGeminiLiveService : GeminiLiveService {

    private lateinit var liveSession: LiveSession
    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var recordBufferSize: Int = 0
    private var currentInputTranscription: String = ""
    private var currentOutputTranscription: String = ""
    private var fullAudioStream: ByteArrayOutputStream? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val transcriptionMutex = Mutex()

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override suspend fun connect(
        config: SessionConfig,
        callbacks: LiveSessionCallbacks
    ): Result<Unit> {
        try {
            val liveGenerationConfig = liveGenerationConfig {
                Napier.d(message = "Setting the AI voice as : ${config.voiceName.name}", tag = TAG)
                speechConfig = SpeechConfig(
                    voice = Voice(config.voiceName.name)
                )
                responseModality = ResponseModality.AUDIO
                inputAudioTranscription = AudioTranscriptionConfig()
                outputAudioTranscription = AudioTranscriptionConfig()
            }

            val liveModel = Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
                modelName = BuildKonfig.LIVE_MODEL_NAME,
                generationConfig = liveGenerationConfig,
                systemInstruction = content {
                    Napier.d(message = "Setting the system instruction as: ${config.systemInstruction}", tag = TAG)
                    text(config.systemInstruction)
                }
            )

            Napier.d(message = "Connecting to Live Session...", tag = TAG)
            liveSession = liveModel.connect()

            callbacks.onOpen()

            setupAudio()
            startRecording(callbacks)
            startReceiving(callbacks)

            return Result.Success(Unit)
        } catch (e: Exception) {
            callbacks.onError(e)
            return Result.Error(e)
        }
    }

    private fun startReceiving(callbacks: LiveSessionCallbacks) {
        serviceScope.launch {
            liveSession.receive()
                .catch { e ->
                    Napier.e(message = "Error receiving callbacks: ${e.message}", tag = TAG, throwable = e)
                    callbacks.onError(e as Exception)
                }
                .collect { response ->
                    Napier.d(message = "Collecting the response...", tag = TAG)
                    handleResponse(response as LiveServerContent, callbacks)
                }
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun setupAudio() {
        Napier.d(message = "Setting up audio...", tag = TAG)

        val sampleRate = 24000
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT

        recordBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            recordBufferSize
        )

        val playbackBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            audioFormat
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(audioFormat)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(playbackBufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        fullAudioStream = ByteArrayOutputStream()
        audioTrack?.play()
    }


    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRecording(callbacks: LiveSessionCallbacks) {
        serviceScope.launch {
            Napier.d("Recording...", tag = TAG)
            audioRecord?.startRecording()

            val buffer = ByteArray(recordBufferSize)
            while (isActive) {
                val read = audioRecord?.read(buffer, 0, recordBufferSize) ?: 0
                if (read > 0) {
                    val audioData = buffer.copyOf(read)
                    // Send audio data to Gemini
                    liveSession.sendAudioRealtime(InlineData(audioData, "audio/pcm"))

                    fullAudioStream?.write(audioData)

                    // Calculate and report volume
                    val volume = calculateVolume(audioData)
                    callbacks.onVolumeUpdate(volume)
                }
            }
        }
    }

    private fun playAudio(data: ByteArray) {
        Napier.d(message = "Playing audio... ${data.size}", tag = TAG)
        audioTrack?.write(data, 0, data.size)
    }

    private fun calculateVolume(audioData: ByteArray): Float {
        // Audio data is PCM 16-bit, so we process it as shorts (2 bytes per sample)
        if (audioData.isEmpty()) return 0f

        val shorts = ShortArray(audioData.size / 2)
        ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts)

        // Calculate Root Mean Square (RMS) of the audio samples
        val rms = sqrt(shorts.map { it.toDouble() * it.toDouble() }.average())

        // Normalize the RMS value. The max value of a short is 32767.
        // This gives a value roughly between 0.0 and 1.0.
        val normalizedRms = rms / 32767.0

        Napier.d(message = "Calculating volume: ${normalizedRms.toFloat()}")

        return normalizedRms.toFloat().coerceIn(0f, 1f)

    }

    @OptIn(ExperimentalTime::class)
    private suspend fun handleResponse(response: LiveServerContent, callbacks: LiveSessionCallbacks) {
        val currentTime = Clock.System.now().toEpochMilliseconds()

        // 1. Handle Audio Playback (Fire and forget)
        response.content?.parts?.filterIsInstance<InlineDataPart>()?.forEach { audioPart ->
            playAudio(audioPart.inlineData)
        }

        // 2. Critical Section for Text
        transcriptionMutex.withLock {
            Napier.d(message = "Response interrupted: ${response.interrupted}", tag = TAG)
            if (response.interrupted) {
                audioTrack?.apply {
                    stop()
                    flush()
                    play()
                }
                currentOutputTranscription = ""
                callbacks.onPartialTranscript("", isUser = false)
            }

            // Handle Model Output
            response.outputTranscription?.let {
                currentOutputTranscription += it.text
                Napier.d(message = "Current Output Transcript: $currentOutputTranscription", tag = TAG)
                callbacks.onPartialTranscript(currentOutputTranscription, isUser = false)
            }

            // Handle User Input (STT)
            response.inputTranscription?.let {
                currentInputTranscription += it.text
                Napier.d(message = "Current Input Transcript: $currentInputTranscription", tag = TAG)
                callbacks.onPartialTranscript(currentInputTranscription, isUser = true)
            }

            Napier.d(message = "Turn Complete: ${response.turnComplete}", tag = TAG)
            if (response.turnComplete) {
                finalizeTurn(currentTime, callbacks)
            }
        }
    }

    private fun finalizeTurn(timestamp: Long, callbacks: LiveSessionCallbacks) {
        if (currentInputTranscription.isNotBlank()) {
            callbacks.onMessage(currentInputTranscription, true, timestamp)
            // Send empty partial to tell UI to hide the "listening" bubble
            callbacks.onPartialTranscript("", isUser = true)
            currentInputTranscription = ""
        }
        if (currentOutputTranscription.isNotBlank()) {
            callbacks.onMessage(currentOutputTranscription, false, timestamp)
            // Send empty partial to tell UI to hide the "thinking" bubble
            callbacks.onPartialTranscript("", isUser = false)
            currentOutputTranscription = ""
        }
    }

    override suspend fun disconnect(): Result<ByteArray?> {
        // Cancel all coroutines started in this scope
        serviceScope.cancel()

        // Clean up resources
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        Napier.d(message = "Releasing Recorder...", tag = TAG)

        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        Napier.d(message = "Releasing Player...", tag = TAG)

        liveSession.close()

        val recordedAudio = fullAudioStream?.toByteArray()
        fullAudioStream?.close()
        fullAudioStream = null

        return Result.Success(recordedAudio)

    }

}