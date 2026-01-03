package com.kmp.talktome.domain.model

import com.kmp.talktome.domain.util.toFormattedDate
import com.kmp.talktome.domain.util.toFormattedTime
import kotlinx.serialization.Serializable
import kotlin.time.Clock

/**
 * Therapy session with full data
 */
@Serializable
data class Session(
    val id: String = "",
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val durationSeconds: Int = 0,
    val analysis: SessionAnalysis? = null,
    val transcript: List<TranscriptMessage> = emptyList(),
    val audioStoragePath: String? = null,
    val userId: String = ""
) {
    /**
     * Get formatted duration string
     */
    fun getFormattedDuration(): String {
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        return if (minutes > 0) {
            "$minutes min ${seconds}s"
        } else {
            "${seconds}s"
        }
    }

    /**
     * Check if session has audio recording
     */
    fun hasAudioRecording(): Boolean = !audioStoragePath.isNullOrBlank()

    /**
     * Get session date formatted
     */
    fun getFormattedDate(): String {
        return timestamp.toFormattedDate()/*java.text.SimpleDateFormat(
            "EEEE, MMMM dd, yyyy",
            java.util.Locale.getDefault()
        ).format(java.util.Date(timestamp))*/
    }

    /**
     * Get session time formatted
     */
    fun getFormattedTime(): String {
        return timestamp.toFormattedTime()/*java.text.SimpleDateFormat(
            "HH:mm",
            java.util.Locale.getDefault()
        ).format(java.util.Date(timestamp))*/
    }
}
