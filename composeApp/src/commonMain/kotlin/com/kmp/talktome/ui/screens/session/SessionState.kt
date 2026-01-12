package com.kmp.talktome.ui.screens.session

import com.kmp.talktome.domain.model.PartialTranscript
import com.kmp.talktome.domain.model.SessionAnalysis
import com.kmp.talktome.domain.model.SessionStatus
import com.kmp.talktome.domain.model.TranscriptMessage
import dev.icerock.moko.permissions.PermissionState

data class SessionState(
    val status: SessionStatus = SessionStatus.IDLE,
    val transcript: List<TranscriptMessage> = emptyList(),
    val partialTranscript: PartialTranscript? = null,
    val volume: Float = 0f,
    val duration: Int = 0,
    val showEndConfirmation: Boolean = false,
    val errorMessage: String? = null,
    val savedSessionId: String? = null,
    val analysis: SessionAnalysis? = null,
    val hasMicrophonePermission: Boolean = false,
    val microphonePermissionState: PermissionState = PermissionState.NotDetermined
)