package com.kmp.talktome.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kmp.talktome.domain.model.Session

/**
 * App navigation destinations
 */
sealed class Screen(val route: String) {

    object Login : Screen("login")

    // Main
    object Home : Screen("home")
    object Profile : Screen("profile")
    object History : Screen("history")

    object Personas : Screen("personas")
    object Session : Screen("session")
    object SessionDetails : Screen("details/{$ARG_SESSION_ID}") {
        fun createRoute(sessionId: String) = "details/$sessionId"
    }

    companion object {
        const val ARG_SESSION_ID = "sessionId"
    }
}

class AppState(val navController: NavHostController) {

    fun navigateBack() {
        navController.popBackStack()
    }

    fun navigateToHome() {
        navController.navigate(Screen.Home.route)
    }

    fun navigateToProfile() {
        navController.navigate(Screen.Profile.route)
    }

    fun navigateToHistory() {
        navController.navigate(Screen.History.route)
    }

    fun navigateToPersonas() {
        navController.navigate(Screen.Personas.route)
    }

    fun navigateToSession() {
        navController.navigate(Screen.Session.route)
    }

    fun navigateToSessionDetails(session: Session) {
        navController.navigate(Screen.SessionDetails.createRoute(session.id))
    }

}


@Composable
fun rememberAppState(myNavController: NavHostController = rememberNavController()) =
    remember {
        AppState(myNavController)
    }

