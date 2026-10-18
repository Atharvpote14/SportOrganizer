
package com.sportorganizer.app.ui.theme.navigation

import com.sportorganizer.app.ui.theme.screens.CollegeOnboardingScreen
import com.sportorganizer.app.ui.theme.screens.PlatformAdminScreen
import com.sportorganizer.app.ui.theme.screens.ProfileScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sportorganizer.app.SportOrganizerHome
import com.sportorganizer.app.ui.screens.AuthScreen
import com.sportorganizer.app.ui.theme.screens.CollegeAdminRequestsScreen

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
        composable(Screen.CollegeOnboarding.route) {
            CollegeOnboardingScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("auth") {
            AuthScreen(
                onAuthenticated = onAuthenticated
            )
        }

        composable(Screen.Dashboard.route) {
            SportOrganizerHome(onOpenProfile = { navController.navigate(Screen.Profile.route) })
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
                onSignOut = onSignOut,
                onOpenCollegeOnboarding = {
                    navController.navigate(Screen.CollegeOnboarding.route)
                },
                onOpenAdminRequests = {
                    navController.navigate(Screen.CollegeAdminRequests.route)
                },
                onOpenPlatformAdmin = {
                    navController.navigate(Screen.PlatformAdmin.route)
                }
            )
        }
        composable(Screen.PlatformAdmin.route) {
            PlatformAdminScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.CollegeAdminRequests.route) {
            CollegeAdminRequestsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
