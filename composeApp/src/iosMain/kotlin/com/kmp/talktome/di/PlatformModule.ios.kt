package com.kmp.talktome.di

import com.kmp.talktome.PermissionHandler
import com.kmp.talktome.SettingsFactory
import com.kmp.talktome.domain.live.AudioPlayer
import com.kmp.talktome.domain.live.AudioRecorder
import com.kmp.talktome.domain.live.GeminiLiveService
import com.kmp.talktome.domain.live.IOSAudioPlayer
import com.kmp.talktome.domain.live.IOSAudioRecorder
import com.kmp.talktome.domain.live.IOSGeminiLiveService
import com.kmp.talktome.domain.live.IOSTextToSpeechEngine
import com.kmp.talktome.domain.live.TextToSpeechEngine
import com.kmp.talktome.domain.notifications.getNotificationManager
import com.kmp.talktome.getPermissionHandler
import com.russhwolf.settings.Settings
import dev.icerock.moko.permissions.PermissionsController
import org.koin.dsl.module

actual fun platformModule() = module {

    single<GeminiLiveService> { IOSGeminiLiveService() }

    single<AudioRecorder> { IOSAudioRecorder() }

    single<AudioPlayer> { IOSAudioPlayer() }

    single<TextToSpeechEngine> { IOSTextToSpeechEngine() }

    single<Settings> { SettingsFactory.createSettings() }

    single { getNotificationManager() }

    single<PermissionHandler> { getPermissionHandler() }

    single<PermissionsController> { PermissionsController(get()) }

}