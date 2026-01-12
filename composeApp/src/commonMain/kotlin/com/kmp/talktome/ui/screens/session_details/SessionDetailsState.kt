package com.kmp.talktome.ui.screens.session_details

import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.model.TtsVoice
import com.kmp.talktome.ui.screens.session_details.models.PlaybackMode

data class SessionDetailsState(
    val session: Session? = null,
    val audioUrl: String? = null,
    val isLoadingAudio: Boolean = false,
    val audioError: Boolean = false,
    val errorMessage: String? = null,
    val isPlaying: Boolean = false,
    val currentTime: Float = 0f,
    val duration: Float = 0f,
    val playbackMode: PlaybackMode = PlaybackMode.RECORDING,
    val availableVoices: List<TtsVoice> = emptyList(),
    val selectedVoice: TtsVoice? = null,
    val ttsCurrentIndex: Int = 0,
    val activeMessageIndex: Int = -1,
    val showSettings: Boolean = false,
    val isDownloading: Boolean = false,
    val transcriptLength: Int = 0
)
