
package com.sportorganizer.app.ui.theme.navigation

import com.sportorganizer.app.ui.screens.ProfileScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sportorganizer.app.SportOrganizerHome
import com.sportorganizer.app.ui.screens.AuthScreen

@Composable
fun SportOrganizerNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Dashboard.route,
    onAuthenticated: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("auth") {
            AuthScreen(
                onAuthenticated = onAuthenticated
            )
        }

        composable(Screen.Dashboard.route) {
            SportOrganizerHome()
        }

        composable(Screen.Teams.route) {
            com.sportorganizer.app.ui.screens.TeamsScreen()
        }

        composable(Screen.Matches.route) {
            PlaceholderScreen(title = Screen.Matches.title)
        }

        composable(Screen.Tournaments.route) {
            PlaceholderScreen(title = Screen.Tournaments.title)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onSignOut = onSignOut
            )
        }
    }
}
