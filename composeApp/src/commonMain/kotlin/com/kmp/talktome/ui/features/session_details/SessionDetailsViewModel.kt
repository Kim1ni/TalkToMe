package com.kmp.talktome.ui.features.session_details

import androidx.lifecycle.ViewModel
import com.kmp.talktome.domain.live.AudioPlayer
import com.kmp.talktome.domain.live.TextToSpeechEngine
import com.kmp.talktome.di.viewModelScope
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.model.TranscriptRole
import com.kmp.talktome.domain.model.TtsVoice
import com.kmp.talktome.domain.repository.SessionRepository
import com.kmp.talktome.domain.util.onSuccess
import com.kmp.talktome.ui.features.session_details.models.PlaybackMode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent


// SessionDetailsViewModel.kt
class SessionDetailsViewModel (
    private val sessionRepository: SessionRepository,
    private val audioPlayer: AudioPlayer,
    private val ttsEngine: TextToSpeechEngine,/*
    private val storageRepository: StorageRepository,
    private val pdfExporter: PdfExporter,*/
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(SessionDetailsState())
    val state: StateFlow<SessionDetailsState> = _state.asStateFlow()

    init {
        setupAudioPlayerCallbacks()
        loadTTSVoices()
    }

    fun loadSession(sessionId: String) {
        viewModelScope.launch {
            sessionRepository.getSessionById(sessionId)
                .onSuccess {session ->
                    _state.update { it.copy(session = session) }
                    session.let { loadAudioFile(it) }

                }
        }
    }

    private suspend fun loadAudioFile(session: Session) {
        _state.update { it.copy(isLoadingAudio = true, audioError = false) }

        session.audioStoragePath?.let { path ->
            try {
                val url = ""//TODO storageRepository.getDownloadUrl(path)
                _state.update {
                    it.copy(
                        audioUrl = url,
                        isLoadingAudio = false
                    )
                }
                audioPlayer.setDataSource(url)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        audioError = true,
                        isLoadingAudio = false,
                        playbackMode = PlaybackMode.TTS
                    )
                }
            }
        } ?: run {
            _state.update {
                it.copy(
                    audioError = true,
                    isLoadingAudio = false,
                    playbackMode = PlaybackMode.TTS
                )
            }
        }
    }

    private fun setupAudioPlayerCallbacks() {
        viewModelScope.launch {
            audioPlayer.currentPosition.collect { position ->
                _state.update { it.copy(currentTime = position / 1000f) }
                updateActiveMessageIndex(position)
            }
        }

        viewModelScope.launch {
            audioPlayer.duration.collect { duration ->
                _state.update { it.copy(duration = duration / 1000f) }
            }
        }

        viewModelScope.launch {
            audioPlayer.isPlaying.collect { isPlaying ->
                if (!isPlaying && _state.value.isPlaying) {
                    _state.update { it.copy(isPlaying = false) }
                }
            }
        }
    }

    private fun loadTTSVoices() {
        viewModelScope.launch(ioDispatcher) {
            val voices = ttsEngine.getAvailableVoices()
            val englishVoices = voices.filter { it.language == "en" }
            val voiceList = englishVoices.ifEmpty { voices }

            _state.update {
                it.copy(
                    availableVoices = voiceList,
                    selectedVoice = voiceList.firstOrNull()
                )
            }
        }
    }

    fun togglePlayback() {
        val currentState = _state.value

        if (currentState.playbackMode == PlaybackMode.RECORDING) {
            if (currentState.audioError) return

            if (currentState.isPlaying) {
                audioPlayer.pause()
            } else {
                audioPlayer.play()
            }
            _state.update { it.copy(isPlaying = !currentState.isPlaying) }
        } else {
            // TTS Mode
            if (currentState.isPlaying) {
                ttsEngine.stop()
                _state.update { it.copy(isPlaying = false) }
            } else {
                playTTS(currentState.ttsCurrentIndex)
                _state.update { it.copy(isPlaying = true) }
            }
        }
    }

    private fun playTTS(index: Int) {
        val session = _state.value.session ?: return
        val transcript = session.transcript

        if (index >= transcript.size) {
            _state.update { it.copy(isPlaying = false, ttsCurrentIndex = 0) }
            return
        }

        val message = transcript[index]
        val voice = _state.value.selectedVoice

        ttsEngine.speak(
            text = message.text,
            voiceName = voice,
            pitch = if (message.role == TranscriptRole.USER) 1.1f else 1.0f,
            rate = 1.0f,
            onComplete = {
                val nextIndex = index + 1
                _state.update { it.copy(ttsCurrentIndex = nextIndex) }

                if (nextIndex < transcript.size && _state.value.isPlaying) {
                    playTTS(nextIndex)
                } else {
                    _state.update { it.copy(isPlaying = false, ttsCurrentIndex = 0) }
                }
            },
            onError = {
                _state.update { it.copy(isPlaying = false) }
            }
        )

        _state.update { it.copy(ttsCurrentIndex = index) }
    }

    fun seekTo(timeSeconds: Float) {
        audioPlayer.seekTo((timeSeconds * 1000).toLong())
    }

    fun switchPlaybackMode(mode: PlaybackMode) {
        audioPlayer.pause()
        ttsEngine.stop()
        _state.update {
            it.copy(
                playbackMode = mode,
                isPlaying = false,
                currentTime = 0f,
                ttsCurrentIndex = 0
            )
        }
    }

    fun selectVoice(voice: TtsVoice) {
        _state.update { it.copy(selectedVoice = voice) }
    }

    fun toggleSettings() {
        _state.update { it.copy(showSettings = !it.showSettings) }
    }

    fun downloadAudio(/*context: Context*/) {
        val audioUrl = _state.value.audioUrl ?: return
        val session = _state.value.session ?: return

        viewModelScope.launch(ioDispatcher) {
            _state.update { it.copy(isDownloading = true) }

            try {
                /*
                storageRepository.downloadFile(
                    url = audioUrl,
                    fileName = "theravoice-session-${session.timestamp}.webm",
                    context = context
                )*/
            } catch (e: Exception) {
                // Show error
            } finally {
                _state.update { it.copy(isDownloading = false) }
            }
        }
    }

    fun exportToPdf(/*context: Context*/) {
        val session = _state.value.session ?: return

        viewModelScope.launch(ioDispatcher) {
            try {
                /*pdfExporter.exportSession(session, context)*/
            } catch (e: Exception) {
                // Show error
            }
        }
    }

    private fun updateActiveMessageIndex(positionMs: Long) {
        val session = _state.value.session ?: return
        val timeSeconds = positionMs / 1000f

        val index = session.transcript.indexOfLast { message ->
            message.timestamp <= timeSeconds
        }

        _state.update { it.copy(activeMessageIndex = index.coerceAtLeast(0)) }
    }

    fun cleanup() {
        audioPlayer.stop()
        audioPlayer.release()
        ttsEngine.stop()
        ttsEngine.shutdown()
    }

    override fun onCleared() {
        super.onCleared()
        cleanup()
    }
}
