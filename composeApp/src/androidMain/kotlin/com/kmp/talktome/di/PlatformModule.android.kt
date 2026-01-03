package com.kmp.talktome.di

import android.content.Context
import com.kmp.talktome.domain.live.AndroidTextToSpeechEngine
import com.kmp.talktome.domain.live.TextToSpeechEngine
import com.kmp.talktome.domain.live.getAudioPlayer
import com.kmp.talktome.domain.live.getAudioRecorder
import com.kmp.talktome.domain.live.getGeminiLiveService
import org.koin.dsl.module

actual fun platformModule() = module {

    single { getGeminiLiveService() }

    single { getAudioRecorder() }

    single { getAudioPlayer() }

    single<TextToSpeechEngine> { AndroidTextToSpeechEngine(get<Context>()) }

}