package com.kmp.talktome.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kmp.talktome.domain.model.Session

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

    fun navigateToSession() {
        navController.navigate(Screen.Session.route)
    }

    fun onSessionSelected(session: Session) {
        navController.navigate(Screen.SessionDetails.createRoute(session.id))
    }
/*
    fun navigateToAnalysis(sessionId: String) {
        navController.navigate(Screen.Analysis.createRoute(sessionId))
    }*/
    fun navigateToAnalysis() {
        navController.navigate(Screen.Analysis.route)
    }

    fun navigateToSessionDetails(sessionId: String) {
        navController.navigate(Screen.SessionDetails.createRoute(sessionId))
    }
}


@Composable
fun rememberAppState(myNavController: NavHostController = rememberNavController()) =
    remember {
        AppState(myNavController)
    }