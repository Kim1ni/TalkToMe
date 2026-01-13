package com.kmp.talktome.di

import androidx.preference.PreferenceManager
import com.kmp.talktome.AndroidLocalNotificationService
import com.kmp.talktome.LocalNotificationService
import com.kmp.talktome.domain.live.AndroidAudioPlayer
import com.kmp.talktome.domain.live.AndroidAudioRecorder
import com.kmp.talktome.domain.live.AndroidGeminiLiveService
import com.kmp.talktome.domain.live.AndroidTextToSpeechEngine
import com.kmp.talktome.domain.live.AudioPlayer
import com.kmp.talktome.domain.live.AudioRecorder
import com.kmp.talktome.domain.live.GeminiLiveService
import com.kmp.talktome.domain.live.TextToSpeechEngine
import com.kmp.talktome.domain.notifications.NotificationManager
import com.kmp.talktome.domain.notifications.getNotificationManager
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.icerock.moko.permissions.PermissionsController
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


actual fun platformModule() = module {

    single<GeminiLiveService> { AndroidGeminiLiveService() }

    single<AudioRecorder> { AndroidAudioRecorder(androidContext()) }

    single<AudioPlayer> { AndroidAudioPlayer() }

    single<TextToSpeechEngine> { AndroidTextToSpeechEngine(androidContext()) }

    single<ObservableSettings> {
        SharedPreferencesSettings(PreferenceManager.getDefaultSharedPreferences(androidContext()))
    }

    single<NotificationManager> { getNotificationManager() }

    single<LocalNotificationService> { AndroidLocalNotificationService(androidContext() ) }
}