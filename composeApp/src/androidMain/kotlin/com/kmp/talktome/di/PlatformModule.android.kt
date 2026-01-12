package com.kmp.talktome.di

import android.app.Application
import com.kmp.talktome.AndroidPermissionHandler
import com.kmp.talktome.PermissionHandler
import com.kmp.talktome.SettingsFactory
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
import com.russhwolf.settings.Settings
import dev.icerock.moko.permissions.PermissionsController
import org.koin.android.ext.koin.androidContext
import org.koin.compose.getKoin
import org.koin.dsl.module


actual fun platformModule() = module {

    single<GeminiLiveService> { AndroidGeminiLiveService() }

    single<AudioRecorder> { AndroidAudioRecorder(androidContext()) }

    single<AudioPlayer> { AndroidAudioPlayer() }

    single<TextToSpeechEngine> { AndroidTextToSpeechEngine(androidContext()) }

    single<Settings> { SettingsFactory.createSettings(androidContext()) }

    single<NotificationManager> { getNotificationManager() }

    single<PermissionHandler> { AndroidPermissionHandler(androidContext()) }

    single<PermissionsController> { PermissionsController(androidContext()) }

}