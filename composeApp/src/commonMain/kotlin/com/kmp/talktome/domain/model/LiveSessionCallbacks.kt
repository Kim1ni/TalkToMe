package com.kmp.talktome.domain.model

interface LiveSessionCallbacks {
    fun onOpen()
    fun onClose()
    fun onError(error: Exception)
    fun onMessage(text: String, isUser: Boolean, timestamp: Long)
    fun onPartialTranscript(text: String?, isUser: Boolean)
    fun onVolumeUpdate(volume: Float)
}
