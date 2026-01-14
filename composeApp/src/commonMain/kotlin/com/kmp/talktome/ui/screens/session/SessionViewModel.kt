package com.kmp.talktome.ui.screens.session

import androidx.lifecycle.ViewModel
import com.kmp.talktome.di.viewModelScope
import com.kmp.talktome.domain.live.GeminiLiveService
import com.kmp.talktome.domain.live.AIVoice
import com.kmp.talktome.domain.model.CustomPersona
import com.kmp.talktome.domain.live.LiveSessionCallbacks
import com.kmp.talktome.domain.model.PartialTranscript
import com.kmp.talktome.domain.model.SessionAnalysis
import com.kmp.talktome.domain.live.SessionConfig
import com.kmp.talktome.domain.model.SessionStatus
import com.kmp.talktome.domain.model.TranscriptMessage
import com.kmp.talktome.domain.model.TranscriptRole
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.repository.PreferencesRepository
import com.kmp.talktome.domain.usecase.session.AnalyzeSessionUseCase
import com.kmp.talktome.domain.usecase.session.EndSessionUseCase
import com.kmp.talktome.domain.usecase.session.SaveSessionUseCase
import com.kmp.talktome.domain.usecase.session.StartSessionUseCase
import com.kmp.talktome.domain.usecase.session.UpdateSessionAnalysisUseCase
import com.kmp.talktome.domain.usecase.todo.CreateTodosFromAnalysisUseCase
import com.kmp.talktome.domain.util.onError
import com.kmp.talktome.domain.util.onSuccess
import dev.icerock.moko.permissions.PermissionState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


private const val TAG = "SessionViewModel"

class SessionViewModel(
    private val startSessionUseCase: StartSessionUseCase,
    private val endSessionUseCase: EndSessionUseCase,
    private val saveSessionUseCase: SaveSessionUseCase,
    private val analyzeSessionUseCase: AnalyzeSessionUseCase,
    private val updateSessionAnalysisUseCase: UpdateSessionAnalysisUseCase,
    private val createTodosFromAnalysisUseCase: CreateTodosFromAnalysisUseCase,
    private val preferencesRepository: PreferencesRepository,
    private val geminiLiveService: GeminiLiveService,
    private val authRepository: AuthRepository,
    //private val permissionHandler: PermissionHandler
): ViewModel() {
    private val _state = MutableStateFlow(SessionState())
    val state: StateFlow<SessionState> = _state.asStateFlow()

    private var durationTimerJob: Job? = null

    init {
        observePermissions()
    }

    private fun observePermissions() {
        viewModelScope.launch {
            //_state.update { it.copy(microphonePermissionState = permissionManager.microphoneState.value) }
//            permissionHandler.observeMicrophonePermission().collect { granted ->
//                _state.update { it.copy(hasMicrophonePermission = granted) }
//            }
        }
    }

    fun onPermissionAction() {
        if (state.value.microphonePermissionState == PermissionState.DeniedAlways) {
            //permissionManager.openAppSettings()
        } else {
            startSession()}
    }
    fun startSession() {
        viewModelScope.launch {
            /*var status = permissionManager.checkMicrophonePermission()
            if (status == PermissionState.NotDetermined) {
                status = permissionManager.requestMicrophonePermission()
            }*/
            val status = PermissionState.Granted

            _state.update { it.copy(microphonePermissionState = status) }

            when (status) {
                PermissionState.Granted -> {
                    proceedWithSessionStart()
                }
                PermissionState.Denied -> {
                    _state.update {
                        it.copy(
                            status = SessionStatus.ERROR,
                            errorMessage = "Microphone permission is required for voice sessions."
                        )
                    }
                }
                PermissionState.DeniedAlways -> {
                    _state.update {
                        it.copy(
                            status = SessionStatus.ERROR,
                            errorMessage = "Microphone access is permanently disabled. Please enable it in system settings."
                        )
                    }
                }
                else -> {
                    // Handle edge cases like Canceled or stay in ERROR Maybe TODO
                }
            }
        }
    }

    /**
     * Separate function to handle the actual connection logic once permissions are cleared.
     */
    private fun proceedWithSessionStart() {
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

                override fun onPartialTranscript(text: String?, isUser: Boolean) {
                    _state.update { current ->
                        current.copy(
                            partialTranscript = if (text.isNullOrBlank()) null
                            else PartialTranscript(
                                role = if (isUser) TranscriptRole.USER else TranscriptRole.MODEL,
                                text = text
                            )
                        )
                    }
                }

                override fun onMessage(text: String, isUser: Boolean, timestamp: Long) {
                    val newMessage = TranscriptMessage(
                        role = if (isUser) TranscriptRole.USER else TranscriptRole.MODEL,
                        text = text,
                        timestamp = timestamp
                    )
                    _state.update { current ->
                        current.copy(
                            transcript = current.transcript + newMessage,
                            partialTranscript = null // Clear the partial once the message is final
                        )
                    }
                }

                override fun onVolumeUpdate(volume: Float) {
                    _state.update { it.copy(volume = volume) }
                }
            }

            startSessionUseCase(config, callbacks)
                .onError { exception ->
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
        Napier.d("confirmEndSession called", tag = TAG)
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                Napier.e("User is not logged in, cannot save session analysis or todos", tag = TAG)
                _state.update { it.copy(status = SessionStatus.ERROR, errorMessage = "User not logged in.") }
                return@launch
            }
            _state.update { it.copy(showEndConfirmation = false, status = SessionStatus.ENDING) }

            stopDurationTimer()

            endSessionUseCase()
                .onSuccess { audioData ->
                    Napier.d("endSessionUseCase success", tag = TAG)
                    // Save session
                    val currentState = _state.value
                    saveSessionUseCase(
                        transcript = currentState.transcript,
                        durationSeconds = currentState.duration,
                        audioData = audioData
                    ).onSuccess { session ->
                        Napier.d("saveSessionUseCase success, session ID: ${session.id}", tag = TAG)
                        _state.update { it.copy(status = SessionStatus.ANALYZING, savedSessionId = session.id) }

                        val conversationText =
                            currentState.transcript.joinToString("\n") { transcriptMessage -> "${transcriptMessage.role}: ${transcriptMessage.text}" }

                        val prompt = """
                            You are an expert therapist supervisor. Analyze the following therapy session transcript.
                            Your analysis should contain:
                            1. A sentiment score (0-100, where 100 is very positive/happy, 0 is very negative/sad).
                            2. A one-word mood description (e.g., "Anxious", "Reflective").
                            3. A brief summary of the session (maximum 2 sentences).
                            4. A list of 2-3 specific areas of improvement for the user (psychological or behavioral).
                            5. A list of 3-5 key topics/keywords discussed.
                            6. A list of 3-5 concrete, actionable "to-do" items. For each item, assign a category from the available options.

                            Transcript:
                            $conversationText
                        """ .trimIndent()
                        Napier.d("Prompt: $prompt", tag = TAG)


                        analyzeSessionUseCase.generateResponse(prompt)
                            .onSuccess { sessionAnalysisString ->
                                Napier.d("Analysis from model: $sessionAnalysisString", tag = TAG)
                                try {
                                    val decodedAnalysis = Json.decodeFromString(
                                        deserializer = SessionAnalysis.serializer(),
                                        string = sessionAnalysisString
                                    )
                                    Napier.d("Decoded analysis: $decodedAnalysis", tag = TAG)
                                    Napier.d("Action Items from analysis: ${decodedAnalysis.actionItems}", tag = TAG)


                                    // Save the analysis
                                    Napier.d("Calling updateSessionAnalysisUseCase for session ID: ${session.id}", tag = TAG)
                                    updateSessionAnalysisUseCase(session.id, decodedAnalysis)
                                        .onSuccess {
                                            Napier.d("Successfully updated session with analysis.", tag = TAG)

                                            // Now create the todos
                                            Napier.d("Calling createTodosFromAnalysisUseCase", tag = TAG)
                                            createTodosFromAnalysisUseCase(decodedAnalysis, session.id, userId)
                                                .onSuccess {
                                                    Napier.d("Successfully created todos from analysis.", tag = TAG)
                                                    _state.update {
                                                        it.copy(
                                                            status = SessionStatus.COMPLETED,
                                                            analysis = decodedAnalysis
                                                        )
                                                    }
                                                }
                                                .onError { exception ->
                                                    _state.update {
                                                        it.copy(
                                                            status = SessionStatus.COMPLETED, // Session is complete, but todo creation failed
                                                            errorMessage = "Failed to create todos: ${exception.message}"
                                                        )
                                                    }
                                                    Napier.e("Failed to create todos from analysis: ${exception.message}", exception, tag = TAG)
                                                }
                                        }
                                        .onError { exception ->
                                            _state.update {
                                                it.copy(
                                                    status = SessionStatus.COMPLETED,
                                                    errorMessage = "Failed to save session analysis: ${exception.message}"
                                                )
                                            }
                                            Napier.e("Failed to save session analysis: ${exception.message}", exception, tag = TAG)
                                        }
                                } catch (e: Exception) {
                                    // JSON parsing failed but session saved
                                    _state.update {
                                        it.copy(
                                            status = SessionStatus.COMPLETED,
                                            errorMessage = "Failed to parse session analysis: ${e.message}"
                                        )
                                    }
                                    Napier.e("Failed to parse session analysis: ${e.message}", e, tag = TAG)
                                }
                            }
                            .onError { exception ->
                                // Analysis generation failed but session saved
                                _state.update {
                                    it.copy(
                                        status = SessionStatus.COMPLETED,
                                        errorMessage = "Failed to analyze session: ${exception.message}"
                                    )
                                }
                                Napier.e("Failed to analyze session: ${exception.message}", exception, tag = TAG)
                            }
                    }.onError { exception ->
                        Napier.e("Failed to save session: ${exception.message}", exception, tag = TAG)
                        _state.update {
                            it.copy(
                                status = SessionStatus.ERROR,
                                errorMessage = "Failed to save session: ${exception.message}"
                            )
                        }
                    }
                }
                .onError { exception ->
                    Napier.e("Failed to end session: ${exception.message}", exception, tag = TAG)
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
                duration = it.duration,
                hasMicrophonePermission = it.hasMicrophonePermission
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

    /**
     * Silently terminates the session and releases hardware.
     * Used for emergency cleanup when the user swiping back or navigates away abruptly.
     */
    fun abandonSession() {
        // 1. Immediately update UI state to prevent further interactions
        _state.update { it.copy(status = SessionStatus.IDLE) }

        // 2. Use a non-cancellable scope to ensure cleanup completes even if VM is clearing
        viewModelScope.launch(NonCancellable) {
            try {
                // Disconnect from Gemini and release mic/speaker hardware
                // We ignore the returned ByteArray because we are abandoning the data
                geminiLiveService.disconnect()

                // stop any internal timers or observers
                stopDurationTimer()
            } catch (e: Exception) {
                // Use Napier or print for silent debugging, don't show to user
                Napier.e("Silent cleanup failed: ${e.message}", tag = TAG)
            }
        }
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
            """ .trimIndent(),
            voiceName = AIVoice.KORE,
            isDefault = true
        )
    }

}