package com.kmp.talktome.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_history
import talktome.composeapp.generated.resources.icon_home
import talktome.composeapp.generated.resources.icon_person_filled
import talktome.composeapp.generated.resources.nav_history
import talktome.composeapp.generated.resources.nav_home
import talktome.composeapp.generated.resources.nav_profile

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = onHomeClick,
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.icon_home),
                    contentDescription = stringResource(Res.string.nav_home),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(stringResource(Res.string.nav_home)) }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.History.route,
            onClick = onHistoryClick,
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.icon_history),
                    contentDescription = stringResource(Res.string.nav_history),
                    modifier = Modifier.size(24.dp),
                )
            },
            label = { Text(stringResource(Res.string.nav_history)) }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Profile.route,
            onClick = onProfileClick,
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.icon_person_filled),
                    contentDescription = stringResource(Res.string.nav_profile),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(stringResource(Res.string.nav_profile)) }
        )
    }
}