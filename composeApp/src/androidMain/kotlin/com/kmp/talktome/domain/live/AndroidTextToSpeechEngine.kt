package com.kmp.talktome.domain.live

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.kmp.talktome.domain.model.TtsVoice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AndroidTextToSpeechEngine(
    context: Context
) : TextToSpeechEngine, TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val isInitialized = MutableStateFlow(false)

    // Using a SupervisorJob so that a failure in one child doesn't cancel the whole scope
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    // Callbacks for speech events
    private var completionCallback: (() -> Unit)? = null
    private var errorCallback: (() -> Unit)? = null

    init {
        // The TextToSpeech constructor is asynchronous. The OnInitListener will be called
        // on the main thread when initialization is complete.
        tts = TextToSpeech(context, this)
    }

    override suspend fun getAvailableVoices(): List<TtsVoice> {
        // Wait until the TTS engine is initialized before querying voices
        isInitialized.first { it }

        return withContext(Dispatchers.IO) {
            tts?.voices?.mapNotNull { platformVoice ->
                // We'll use the voice name as a unique identifier
                TtsVoice(id = platformVoice.name, name = platformVoice.name, language = platformVoice.locale.language)
            } ?: emptyList()
        }
    }

    override fun speak(
        text: String,
        voiceName: TtsVoice?,
        pitch: Float,
        rate: Float,
        onComplete: () -> Unit,
        onError: () -> Unit
    ) {
        scope.launch {
            // Wait for initialization if not ready yet
            isInitialized.first { it }

            completionCallback = onComplete
            errorCallback = onError

            tts?.let { engine ->
                // Set voice if specified
                voiceName?.let { name ->
                    engine.voices.find { it.name == name.id }?.let {
                        engine.voice = it
                    }
                }

                engine.setPitch(pitch.coerceIn(0.1f, 2.0f))
                engine.setSpeechRate(rate.coerceIn(0.1f, 2.0f))


                // Use an utterance ID to track progress
                val utteranceId = text.hashCode().toString()
                val params = Bundle().apply {
                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
                }

                engine.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
            } ?: onError()
        }
    }

    override fun stop() {
        tts?.stop()
        completionCallback = null
        errorCallback = null
    }

    override fun shutdown() {
        // Cancel all coroutines within this scope
        scope.coroutineContext[Job]?.cancel()
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized.value = false
    }

    /**
     * Called when the TTS engine has finished initialization.
     */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized.value = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    // Called when speech starts
                }

                override fun onDone(utteranceId: String?) {
                    scope.launch {
                        completionCallback?.invoke()
                        completionCallback = null
                    }
                }

                override fun onError(utteranceId: String?) {
                    scope.launch {
                        errorCallback?.invoke()
                        errorCallback = null
                    }
                }
            })
        } else {
            // Initialization failed
            isInitialized.value = false
        }
    }
}
