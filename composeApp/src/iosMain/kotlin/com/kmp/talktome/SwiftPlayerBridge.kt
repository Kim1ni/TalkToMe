package com.kmp.talktome

import kotlinx.coroutines.flow.StateFlow

interface SwiftPlayerBridge {

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