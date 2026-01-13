package com.kmp.talktome.di

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManager.Listener
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

expect fun platformModule(): Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration.invoke(this)
        modules(
            modules = listOf(
                platformModule(),
                dataModule,
                domainModule,
                uiModule
            )
        )
    }
}

private const val TAG = "KMPNotifier"

private fun initNotifier(
    configuration: NotificationPlatformConfiguration,
) {
    NotifierManager.initialize(configuration)
    NotifierManager.addListener(object : Listener {

        override fun onNotificationClicked(data: PayloadData) {
            super.onNotificationClicked(data)
            Napier.d("Notification clicked with $data", tag = TAG)


            val sessionId = data["reflectionId"] as? String
            if (sessionId != null) {
                Napier.d("Navigating to session: $sessionId", tag = TAG)
                //navigateToAddReflection(SessionId(sessionId))
                return
            }

            Napier.d("No data to navigate with, ignoring notification", tag = TAG)
        }

        override fun onNewToken(token: String) {
            Napier.d("New token received: $token", tag = TAG)
        }
    })
}
