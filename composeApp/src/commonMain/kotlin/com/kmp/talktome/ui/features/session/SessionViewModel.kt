package com.kmp.talktome.ui.features.session

import androidx.lifecycle.ViewModel
import com.kmp.talktome.domain.model.AIVoice
import com.kmp.talktome.domain.model.CustomPersona
import com.kmp.talktome.domain.model.LiveSessionCallbacks
import com.kmp.talktome.domain.model.PartialTranscript
import com.kmp.talktome.domain.model.SessionConfig
import com.kmp.talktome.domain.model.SessionStatus
import com.kmp.talktome.domain.model.TranscriptMessage
import com.kmp.talktome.domain.model.TranscriptRole
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.repository.PreferencesRepository
import com.kmp.talktome.domain.usecase.session.AnalyzeSessionUseCase
import com.kmp.talktome.domain.usecase.session.EndSessionUseCase
import com.kmp.talktome.domain.usecase.session.SaveSessionUseCase
import com.kmp.talktome.domain.usecase.session.StartSessionUseCase
import com.kmp.talktome.domain.util.onError
import com.kmp.talktome.domain.util.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class SessionViewModel(
    private val startSessionUseCase: StartSessionUseCase,
    private val endSessionUseCase: EndSessionUseCase,
    private val saveSessionUseCase: SaveSessionUseCase,
    private val analyzeSessionUseCase: AnalyzeSessionUseCase,
    private val preferencesRepository: PreferencesRepository,
    private val authRepository: AuthRepository,
    private val viewModelScope: CoroutineScope
): ViewModel(), KoinComponent {
    private val _state = MutableStateFlow(SessionState())
    val state: StateFlow<SessionState> = _state.asStateFlow()

    private var durationTimerJob: Job? = null

    fun startSession() {
        viewModelScope.launch {
            _state.update { it.copy(status = SessionStatus.CONNECTING) }

            // Get active persona config
            val userId = authRepository.getCurrentUserId() ?: return@launch
            val preferences = preferencesRepository.getUserPreferences(userId)
                .firstOrNull() ?: return@launch

            val activePersona = preferences.let { cloud ->
                cloud.customPersonas.find { it.id == cloud.activePersonaId }
            } ?: getDefaultPersona()

            val config = SessionConfig(
                systemInstruction = activePersona.instructions,
                voiceName = activePersona.voiceName
            )

            val callbacks = object : LiveSessionCallbacks {

                override fun onOpen() {
                    _state.update { it.copy(status = SessionStatus.ACTIVE) }
                    startDurationTimer()
                }

                override fun onClose() {
                    _state.update { it.copy(status = SessionStatus.ENDED) }
                    stopDurationTimer()
                }

                override fun onError(error: Exception) {
                    _state.update {
                        it.copy(
                            status = SessionStatus.ERROR,
                            errorMessage = error.message ?: "Connection error"
                        )
                    }
                    stopDurationTimer()
                }

                override fun onMessage(text: String, isUser: Boolean, timestamp: Long) {
                    val message = TranscriptMessage(
                        role = if (isUser) TranscriptRole.USER else TranscriptRole.MODEL,
                        text = text,
                        timestamp = timestamp
                    )
                    _state.update { current ->
                        current.copy(
                            transcript = current.transcript + message,
                            partialTranscript = null
                        )
                    }
                }

                override fun onPartialTranscript(text: String?, isUser: Boolean) {
                    _state.update {
                        it.copy(
                            partialTranscript = text?.let { txt ->
                                PartialTranscript(
                                    role = if (isUser) TranscriptRole.USER else TranscriptRole.MODEL,
                                    text = txt
                                )
                            }
                        )
                    }
                }

                override fun onVolumeUpdate(volume: Float) {
                    _state.update { it.copy(volume = volume) }
                }

            }

            startSessionUseCase(config, callbacks)
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            status = SessionStatus.ERROR,
                            errorMessage = exception.message
                        )
                    }
                }
        }
    }

    fun endSession() {
        if (_state.value.status != SessionStatus.ACTIVE) return

        _state.update { it.copy(showEndConfirmation = true) }
    }

    fun confirmEndSession() {
        viewModelScope.launch {
            _state.update { it.copy(showEndConfirmation = false, status = SessionStatus.ENDING) }

            stopDurationTimer()

            endSessionUseCase()
                .onSuccess { audioData ->
                    // Save session
                    val currentState = _state.value
                    saveSessionUseCase(
                        transcript = currentState.transcript,
                        durationSeconds = currentState.duration,
                        audioData = audioData
                    ).onSuccess { session ->
                        // Analyze session
                        analyzeSessionUseCase(session)
                            .onSuccess { analysis ->
                                _state.update {
                                    it.copy(
                                        status = SessionStatus.COMPLETED,
                                        savedSessionId = session.id,
                                        analysis = analysis
                                    )
                                }
                            }
                            .onFailure { exception ->
                                // Analysis failed but session saved
                                _state.update {
                                    it.copy(
                                        status = SessionStatus.COMPLETED,
                                        savedSessionId = session.id,
                                        errorMessage = "Failed to analyze session: ${exception.message}"
                                    )
                                }
                            }
                    }.onError { exception ->
                        _state.update {
                            it.copy(
                                status = SessionStatus.ERROR,
                                errorMessage = "Failed to save session: ${exception.message}"
                            )
                        }
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            status = SessionStatus.ERROR,
                            errorMessage = exception.message
                        )
                    }
                }
        }
    }

    fun cancelEndSession() {
        _state.update { it.copy(showEndConfirmation = false) }
    }

    fun retryConnection() {
        _state.update {
            SessionState(
                transcript = it.transcript,
                duration = it.duration
            )
        }
        startSession()
    }

    private fun startDurationTimer() {
        durationTimerJob = viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                _state.update { it.copy(duration = it.duration + 1) }
            }
        }
    }

    private fun stopDurationTimer() {
        durationTimerJob?.cancel()
        durationTimerJob = null
    }

    private fun getDefaultPersona(): CustomPersona {
        return CustomPersona(
            id = "empathetic",
            name = "Gentle Listener",
            description = "Warm, compassionate, and validating.",
            instructions = """
                You are a warm, empathetic, and compassionate therapist. 
                Your goal is to listen effectively, provide deep validation, 
                and create a safe space. Focus on emotions and support. 
                Keep responses concise and conversational.
            """.trimIndent(),
            voiceName = AIVoice.KORE,
            isDefault = true
        )
    }

}