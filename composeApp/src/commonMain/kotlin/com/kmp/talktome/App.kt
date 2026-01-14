package com.kmp.talktome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kmp.talktome.domain.model.Theme
import com.kmp.talktome.domain.repository.SettingsRepository
import com.kmp.talktome.ui.navigation.SetUpNavGraph
import com.kmp.talktome.ui.theme.TalkToMeTheme
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import org.koin.compose.koinInject

@Composable
fun App(
    onThemeChange: ((isDarkTheme: Boolean) -> Unit)? = null,
) {
    val settings = koinInject<SettingsRepository>()
    val currentTheme by settings.getTheme().collectAsStateWithLifecycle(initialValue = Theme.SYSTEM)
    val isDarkTheme = when (currentTheme) {
        Theme.LIGHT -> false
        Theme.DARK -> true
        Theme.SYSTEM -> isSystemInDarkTheme()
    }

    if (onThemeChange != null) {
        LaunchedEffect(isDarkTheme) { onThemeChange(isDarkTheme) }
    }

    TalkToMeTheme(darkTheme = isDarkTheme) {
        var appReady by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            GoogleAuthProvider.create(
                credentials = GoogleAuthCredentials(serverId = BuildKonfig.WEB_CLIENT_ID)
            )
            appReady = true
        }

        AnimatedVisibility(
            visible = appReady,
            modifier = Modifier.fillMaxSize()
        ) {
            SetUpNavGraph()
        }
    }
}