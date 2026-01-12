package com.kmp.talktome

import android.app.Application
import com.kmp.talktome.di.initKoin
import com.kmp.talktome.domain.notifications.initNotificationManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent

class MyApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        initPermissionHandler(this)
        initNotificationManager(this)

        Firebase.initialize(context = this)

        initKoin {
            androidLogger()
            androidContext(this@MyApplication)
        }

        Napier.base(DebugAntilog())
    }
}