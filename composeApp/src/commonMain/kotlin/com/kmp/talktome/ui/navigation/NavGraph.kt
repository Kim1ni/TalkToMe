package com.kmp.talktome.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kmp.talktome.ui.composables.BottomNavigationBar
import com.kmp.talktome.ui.screens.history.HistoryScreen
import com.kmp.talktome.ui.screens.home.HomeScreen
import com.kmp.talktome.ui.screens.login.LoginScreen
import com.kmp.talktome.ui.screens.persona.PersonaScreen
import com.kmp.talktome.ui.screens.profile.ProfileScreen
import com.kmp.talktome.ui.screens.session.SessionScreen
import com.kmp.talktome.ui.screens.session_details.SessionDetailsScreen
import com.kmp.talktome.ui.theme.TalkToMeTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_app_icon

@Composable
fun SetUpNavGraph(
    appState: AppState = rememberAppState(),
    viewModel: AuthViewModel = koinViewModel<AuthViewModel>(),
) {
    val authState by viewModel.state.collectAsState()

    val startDestination = when (authState) {
        AuthState.AUTHENTICATED -> Screen.Home.route
        AuthState.UNAUTHENTICATED -> Screen.Login.route
        AuthState.LOADING -> Screen.Login.route
    }

    if (authState == AuthState.LOADING) {
        SplashScreen()
    } else {
        AppScaffold(
            appState = appState,
            startDestination = startDestination
        )
    }
}


@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Your App Logo
            Image(
                painter = painterResource(Res.drawable.icon_app_icon),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun AppScaffold(
    appState: AppState,
    startDestination: String
) {

    val navBackStackEntry by appState.navController.currentBackStackEntryFlow.collectAsState(null)
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.History.route,
        Screen.Profile.route
    )

    val showBottomBar = currentRoute in bottomBarRoutes


    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onHomeClick = appState::navigateToHome,
                    onHistoryClick = appState::navigateToHistory,
                    onProfileClick = appState::navigateToProfile
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = appState.navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = appState::navigateToHome
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onStartSession = appState::navigateToSession,
                )
            }
            composable(Screen.Session.route) {
                SessionScreen(
                    onSessionComplete = appState::navigateToHome,
                    onCancel = appState::navigateBack
                )
            }

            composable(
                route = Screen.SessionDetails.route,
                arguments = listOf(navArgument(Screen.ARG_SESSION_ID) { type = NavType.StringType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString(Screen.ARG_SESSION_ID) ?: ""

                SessionDetailsScreen(
                    sessionId = sessionId,
                    onBackPressed = appState::navigateBack
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToPersonas = appState::navigateToPersonas
                )
            }
            composable(Screen.Personas.route) {
                PersonaScreen(
                    onBackPressed = appState::navigateBack
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateToSessionDetails = appState::navigateToSessionDetails
                )
            }
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    TalkToMeTheme {
        SplashScreen()
    }
}