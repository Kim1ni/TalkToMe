package com.kmp.talktome

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mmk.kmpnotifier.extensions.onCreateOrOnNewIntent
import com.mmk.kmpnotifier.notification.NotifierManager
import org.koin.android.ext.android.inject
import org.koin.mp.KoinPlatform

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        processIntent(intent)

        KoinPlatform.getKoin().declare(PermissionHandler(activity = this))
        setContent {
            App(
                onThemeChange = { isDarkMode ->
                    val systemBarStyle = SystemBarStyle.auto(
                        lightScrim = Color.TRANSPARENT,
                        darkScrim = Color.TRANSPARENT,
                        detectDarkMode = { isDarkMode }
                    )
                    enableEdgeToEdge(
                        statusBarStyle = systemBarStyle,
                        navigationBarStyle = systemBarStyle,
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        window.isNavigationBarContrastEnforced = false
                    }
                }
            )
        }
    }

    private fun processIntent(intent: Intent?) {
        if (intent == null) return

        try {
            val notificationId = intent.getStringExtra(EXTRA_LOCAL_NOTIFICATION_ID)
            if (notificationId != null) {
                // Local notification clicked
                //TODO navigateByLocalNotificationId(notificationId)
                return
            }

            // Process push notifications
            NotifierManager.onCreateOrOnNewIntent(intent)
        } catch (e: Exception) {
            return
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}