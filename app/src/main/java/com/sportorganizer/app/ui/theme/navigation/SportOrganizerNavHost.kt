package com.sportorganizer.app.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sportorganizer.app.SportOrganizerHome

@Composable
fun SportOrganizerNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
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
            PlaceholderScreen(title = Screen.Profile.title)
        }
    }
}
