package com.kmp.talktome.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class TranscriptMessage(
    val role: TranscriptRole,
    val text: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
) {/*
    fun getFormattedTime(): String {
        val seconds = (timestamp / 1000).toInt()
        val minutes = seconds / 60
        val secs = seconds % 60
        return "%d:%02d".format(minutes, secs)
    }*/
}

enum class TranscriptRole {
    USER, MODEL;

    companion object {
        fun fromString(value: String?): TranscriptRole? {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            }
        }
    }
}