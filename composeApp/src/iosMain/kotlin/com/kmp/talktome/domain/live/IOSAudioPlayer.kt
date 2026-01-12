package com.kmp.talktome.domain.live

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSURL


@OptIn(ExperimentalForeignApi::class)
class IOSAudioPlayer : AudioPlayer {

    private var player: AVPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    override suspend fun setDataSource(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        val playerItem = AVPlayerItem(uRL = nsUrl)

        if (player == null) {
            player = AVPlayer(playerItem = playerItem)
        } else {
            player?.replaceCurrentItemWithPlayerItem(playerItem)
        }

        // Reset states
        _currentPosition.value = 0L
        _duration.value = 0L
    }

    override fun play() {
        player?.play()
        _isPlaying.value = true
        startTimer()
    }

    override fun pause() {
        player?.pause()
        _isPlaying.value = false
        stopTimer()
    }

    override fun seekTo(position: Long) {
        val seconds = position / 1000.0
        val time = CMTimeMakeWithSeconds(seconds, 1000)
        player?.seekToTime(time)
    }

    override fun stop() {
        player?.pause()
        player?.seekToTime(CMTimeMakeWithSeconds(0.0, 1000))
        _isPlaying.value = false
        stopTimer()
    }

    override fun release() {
        stop()
        player = null
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (true) {
                player?.let { p ->
                    val current = CMTimeGetSeconds(p.currentTime())
                    _currentPosition.value = (current * 1000).toLong()

                    val duration = p.currentItem?.duration?.let { CMTimeGetSeconds(it) } ?: 0.0
                    if (!duration.isNaN()) {
                        _duration.value = (duration * 1000).toLong()
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
}
