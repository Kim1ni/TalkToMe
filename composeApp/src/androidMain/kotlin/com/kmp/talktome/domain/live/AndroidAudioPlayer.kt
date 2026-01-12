package com.kmp.talktome.domain.live

import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AndroidAudioPlayer : AudioPlayer, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var positionJob: Job? = null

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private fun initializePlayer() {
        release() // Release any existing player
        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener(this@AndroidAudioPlayer)
            setOnCompletionListener(this@AndroidAudioPlayer)
        }
    }

    override suspend fun setDataSource(url: String) {
        initializePlayer()
        mediaPlayer?.apply {
            setDataSource(url)
            prepareAsync()
        }
    }

    override fun play() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _isPlaying.value = true
                startPositionUpdates()
            }
        }
    }

    override fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
                stopPositionUpdates()
            }
        }
    }

    override fun seekTo(position: Long) {
        mediaPlayer?.seekTo(position.toInt())
        _currentPosition.value = position
    }

    override fun stop() {
        mediaPlayer?.stop()
        _isPlaying.value = false
        _currentPosition.value = 0L
        stopPositionUpdates()
    }

    override fun release() {
        stopPositionUpdates()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        _currentPosition.value = 0L
        _duration.value = 0L
    }

    override fun onPrepared(mp: MediaPlayer?) {
        _duration.value = mp?.duration?.toLong() ?: 0L
        // Removed auto-play: let the user press the play button
    }

    override fun onCompletion(mp: MediaPlayer?) {
        _isPlaying.value = false
        _currentPosition.value = _duration.value
        stopPositionUpdates()
    }

    private fun startPositionUpdates() {
        positionJob?.cancel()
        positionJob = scope.launch {
            while (_isPlaying.value) {
                mediaPlayer?.let {
                    _currentPosition.value = it.currentPosition.toLong()
                }
                delay(100L)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionJob?.cancel()
        positionJob = null
    }
}
