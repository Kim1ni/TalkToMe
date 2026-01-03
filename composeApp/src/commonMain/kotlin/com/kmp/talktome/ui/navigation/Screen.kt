package com.kmp.talktome.ui.navigation

/**
 * App navigation destinations
 */
sealed class Screen(val route: String) {

    object Login : Screen("login")

    // Main
    object Home : Screen("home")

    object Profile : Screen("profile")

    object History : Screen("history")

    object Session : Screen("session")

    object Analysis : Screen("analysis/{$ARG_SESSION_ID}") {
        fun createRoute(sessionId: String) = "analysis/$sessionId"
    }

    object SessionDetails : Screen("details/{$ARG_SESSION_ID}") {
        fun createRoute(sessionId: String) = "details/$sessionId"
    }

    companion object {
        const val ARG_SESSION_ID = "sessionId"
    }
}