package com.kmp.talktome.domain.live

import com.kmp.talktome.SwiftPlayerBridge
import kotlinx.coroutines.flow.StateFlow

class IOSAudioPlayer : AudioPlayer {

    var bridge: SwiftPlayerBridge? = null
    
    override val currentPosition: StateFlow<Long>
        get() = TODO("Not yet implemented")

    override val duration: StateFlow<Long>
        get() = TODO("Not yet implemented")

    override val isPlaying: StateFlow<Boolean>
        get() = TODO("Not yet implemented")

    override suspend fun setDataSource(url: String) {
        bridge?.setDataSource(url)
    }

    override fun play() {
        bridge?.play()
    }

    override fun pause() {
        bridge?.pause()
    }

    override fun seekTo(position: Long) {
        bridge?.seekTo(position)
    }

    override fun stop() {
        bridge?.stop()
    }

    override fun release() {
        bridge?.release()
    }
    
}

actual fun getAudioPlayer(): AudioPlayer = IOSAudioPlayer()