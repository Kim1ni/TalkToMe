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

/*
@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}
*/

/*
@Composable
fun App() {
    // 1. Create the backstack (Starts with Login)
    val backStack = rememberNavBackStack(startDestination = Screen.Login)

    // 2. Define how keys map to UI
    NavDisplay(
        backstack = backStack,
        entryProvider = entryProvider {
            entry<Screen.Login> {
                LoginScreen(
                    onNavigateToHome = {
                        // Clear backstack and set Home as the new root
                        backStack.setStack(listOf(Screen.Home))
                    }
                )
            }
            entry<Screen.Home> {
                HomeScreen()
            }
        }
    )
}*/

@Composable
fun App() {
    TalkToMeTheme{
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