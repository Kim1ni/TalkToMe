package com.kmp.talktome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kmp.talktome.ui.navigation.SetUpNavGraph
import com.kmp.talktome.ui.theme.TalkToMeTheme
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider

@Composable
fun App() {
    TalkToMeTheme {
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