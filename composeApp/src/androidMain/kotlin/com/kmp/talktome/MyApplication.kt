package com.kmp.talktome

import android.app.Application
import com.kmp.talktome.di.initKoin
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent

class MyApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        Firebase.initialize(context = this)

        initKoin {
            androidLogger()
            androidContext(this@MyApplication)
        }
    }
}