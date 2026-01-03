@file: OptIn(PublicPreviewAPI::class)

package com.kmp.talktome.domain.live

import android.Manifest
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AndroidGeminiLiveService : GeminiLiveService {

    private lateinit var liveSession: LiveSession
    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var recordBufferSize: Int = 0
    private var currentModelTranscript: String = ""
    private var fullAudioStream: ByteArrayOutputStream? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override suspend fun connect(
        config: SessionConfig,
        callbacks: LiveSessionCallbacks
    ): Result<Unit> {
        try {
            val liveGenerationConfig = liveGenerationConfig {
                speechConfig = SpeechConfig(
                    voice = Voice(config.voiceName.name)
                )
                responseModality = ResponseModality.AUDIO
            }

            val liveModel = Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
                modelName = BuildKonfig.LIVE_MODEL_NAME,
                generationConfig = liveGenerationConfig,
                systemInstruction = content {
                    text(config.systemInstruction)
                }
            )

            liveSession = liveModel.connect()

            setupAudio()
            startRecording(callbacks)
            startReceiving(callbacks)

            callbacks.onOpen()
            return Result.success(Unit)
        } catch (e: Exception) {
            callbacks.onError(e)
            return Result.failure(e)
        }
    }

    private fun startReceiving(callbacks: LiveSessionCallbacks) {
        serviceScope.launch {
            liveSession.receive()
                .catch { e -> callbacks.onError(e as Exception) }
                .collect { response ->
                    handleResponse(response as LiveServerContent, callbacks)
                }
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun setupAudio() {
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
        return normalizedRms.toFloat().coerceIn(0f, 1f)
    }

    @OptIn(ExperimentalTime::class)
    private fun handleResponse(response: LiveServerContent, callbacks: LiveSessionCallbacks) {
        // Play back audio from the model
        response.content?.parts?.filterIsInstance<InlineDataPart>()?.forEach { audioPart ->
            playAudio(audioPart.inlineData)
        }

        if (response.interrupted) {
            // Stop any ongoing playback if the user interrupts
            audioTrack?.stop()
            audioTrack?.flush()
            audioTrack?.play()
        }

        // Handle Transcripts for UI updates
        response.outputTranscription?.text?.let {
            currentModelTranscript = it
            Log.d("AndroidGeminiLiveService", "Output Transcription: $it")
            callbacks.onPartialTranscript(it, isUser = false)
        }

        response.inputTranscription?.text?.let {
            callbacks.onPartialTranscript(it, isUser = true)
        }

        // When a full "turn" is done, finalize the message in the transcript
        if (response.turnComplete) {
            val isUser = response.content?.role == "user"
            val text = if (isUser) {
                response.inputTranscription?.text
            } else {
                currentModelTranscript
            }

            if (text != null) {
                callbacks.onMessage(
                    text = text,
                    isUser = isUser,
                    timestamp = Clock.System.now().toEpochMilliseconds()
                )
            }

            if (!isUser) {
                currentModelTranscript = ""
            }
        }
    }

    override suspend fun disconnect(): Result<ByteArray?> {
        // Cancel all coroutines started in this scope
        serviceScope.cancel()

        // Clean up resources
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null

        liveSession.close()

        val recordedAudio = fullAudioStream?.toByteArray()
        fullAudioStream?.close()
        fullAudioStream = null

        return Result.success(recordedAudio)

    }

}

actual fun getGeminiLiveService(): GeminiLiveService = AndroidGeminiLiveService()
