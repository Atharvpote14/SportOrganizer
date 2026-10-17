package com.sportorganizer.app.ui.theme.navigation

sealed class Screen(
    val route: String,
    val title: String
) {
    data object Dashboard : Screen(
        route = "dashboard",
        title = "Dashboard"
    )

    data object Teams : Screen(
        route = "teams",
        title = "Teams"
    )

    data object Matches : Screen(
        route = "matches",
        title = "Matches"
    )

    data object Tournaments : Screen(
        route = "tournaments",
        title = "Tournaments"
    )

    data object Profile : Screen(
        route = "profile",
        title = "Profile"
    )
}
