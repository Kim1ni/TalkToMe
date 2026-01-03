package com.kmp.talktome.di

import com.kmp.talktome.domain.live.getAudioPlayer
import com.kmp.talktome.domain.live.getAudioRecorder
import com.kmp.talktome.domain.live.getGeminiLiveService
import com.kmp.talktome.domain.live.getTextToSpeechEngine
import org.koin.dsl.module

actual fun platformModule() = module {

    single { getGeminiLiveService() }

    single { getAudioRecorder() }

    single { getAudioPlayer() }

    single { getTextToSpeechEngine() }

}