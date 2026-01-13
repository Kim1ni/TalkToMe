package com.kmp.talktome.di

import com.kmp.talktome.IOSLocalNotificationService
import com.kmp.talktome.LocalNotificationService
import com.kmp.talktome.domain.live.AudioPlayer
import com.kmp.talktome.domain.live.AudioRecorder
import com.kmp.talktome.domain.live.GeminiLiveService
import com.kmp.talktome.domain.live.IOSAudioPlayer
import com.kmp.talktome.domain.live.IOSAudioRecorder
import com.kmp.talktome.domain.live.IOSGeminiLiveService
import com.kmp.talktome.domain.live.IOSTextToSpeechEngine
import com.kmp.talktome.domain.live.TextToSpeechEngine
import com.kmp.talktome.domain.notifications.getNotificationManager
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import dev.icerock.moko.permissions.PermissionsController
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual fun platformModule() = module {

    single<GeminiLiveService> { IOSGeminiLiveService() }

    single<AudioRecorder> { IOSAudioRecorder() }

    single<AudioPlayer> { IOSAudioPlayer() }

    single<TextToSpeechEngine> { IOSTextToSpeechEngine() }

    single<ObservableSettings> {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }

    single { getNotificationManager() }

    single<LocalNotificationService> { IOSLocalNotificationService() }

}