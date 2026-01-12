package com.kmp.talktome.domain.live

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {

    val currentPosition: StateFlow<Long>

    val duration: StateFlow<Long>

    val isPlaying: StateFlow<Boolean>

    suspend fun setDataSource(url: String)

    fun play()

    fun pause()

    fun seekTo(position: Long)

    fun stop()

    fun release()

}
