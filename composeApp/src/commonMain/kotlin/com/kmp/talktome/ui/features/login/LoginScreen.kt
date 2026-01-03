package com.kmp.talktome.ui.features.login

import MessageBarState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.talktome.ui.features.login.composables.AuthButton
import com.kmp.talktome.ui.features.login.composables.OrDivider
import com.kmp.talktome.ui.theme.TalkToMeTheme
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.app_logo_description
import talktome.composeapp.generated.resources.app_name
import talktome.composeapp.generated.resources.icon_app_icon
import talktome.composeapp.generated.resources.icon_google
import talktome.composeapp.generated.resources.icon_guest
import talktome.composeapp.generated.resources.login_disclaimer
import talktome.composeapp.generated.resources.login_google_button
import talktome.composeapp.generated.resources.login_guest_button

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel<LoginViewModel>(),
    onLoginSuccess: () -> Unit
) {
    val messageBarState = remember { MessageBarState() }
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsState()
    var idToken = ""

    state.errorMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            viewModel.dismissError()
        }
    }

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onLoginSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(0.9f),
                color = Color.White,
                shape = RoundedCornerShape(24.dp)
            ) {
                GoogleButtonUiContainerFirebase(
                    onResult = { result ->
                        result.onSuccess { user ->
                            messageBarState.addSuccess("Logged in successfully")
                            runBlocking { user?.getIdToken(true)?.let { idToken = it } }
                            viewModel.signInWithGoogle(idToken)
                        }.onFailure { error ->
                            if(error.message?.contains("A network error") == true) {
                                 messageBarState.addError("No internet connection")
                            } else if (error.message?.contains("idToken is null") == true) {
                                messageBarState.addError("Google login failed")
                            } else {
                                messageBarState.addError(error.message ?: "Unknown error")
                            }
                        }
                    },
                    linkAccount = false
                ) {
                    LoginScreenContent(
                        onGoogleClick = { this@GoogleButtonUiContainerFirebase.onClick() },
                        onGuestClick = { viewModel.signInAnonymously() },
                        isGoogleLoading = state.isGoogleLoading,
                        isGuestLoading = state.isGuestLoading
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreenContent(
    onGoogleClick: () -> Unit,
    onGuestClick: () -> Unit,
    isGoogleLoading: Boolean,
    isGuestLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        AppLogo()

        AuthActions(
            modifier = Modifier.weight(1f),
            onGoogleSignIn = onGoogleClick,
            onGuestSignIn = onGuestClick,
            isGoogleLoading = isGoogleLoading,
            isGuestLoading = isGuestLoading
        )
    }
}
@Composable
fun AuthActions(
    modifier: Modifier = Modifier,
    onGoogleSignIn: () -> Unit,
    onGuestSignIn: () -> Unit,
    isGoogleLoading: Boolean,
    isGuestLoading: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        AuthButton(
            onClick = onGoogleSignIn,
            text = stringResource(Res.string.login_google_button),
            icon = painterResource(Res.drawable.icon_google),
            isLoading = isGoogleLoading,
            contentDescription = stringResource(Res.string.login_google_button)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OrDivider()

        Spacer(modifier = Modifier.height(12.dp))

        AuthButton(
            onClick = onGuestSignIn,
            text = stringResource(Res.string.login_guest_button),
            icon = painterResource(Res.drawable.icon_guest),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
            isLoading = isGuestLoading,
            contentDescription = stringResource(Res.string.login_guest_button)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.login_disclaimer),
            fontWeight = FontWeight.ExtraLight,
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    TalkToMeTheme {
        LoginScreen(
            onLoginSuccess = {}
        )
    }
}

@Composable
private fun AppLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(0.72f),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.icon_app_icon),
            contentDescription = stringResource(Res.string.app_logo_description),
            modifier = Modifier.size(90.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.app_name),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TalkToMeTheme.extendedColorScheme.actionGreen.color
        )
    }
}
