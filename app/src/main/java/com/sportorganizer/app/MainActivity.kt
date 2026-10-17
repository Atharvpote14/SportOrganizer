
package com.sportorganizer.app

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.sportorganizer.app.data.remote.repository.AuthRepository
import android.os.Bundle
import androidx.activity.ComponentActivity
import io.github.jan.supabase.auth.auth
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sportorganizer.app.ui.screens.AuthScreen
import com.sportorganizer.app.ui.screens.DashboardScreen
import com.sportorganizer.app.ui.theme.SportOrganizerTheme
import com.sportorganizer.app.ui.theme.navigation.Screen
import com.sportorganizer.app.ui.theme.navigation.SportOrganizerNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SportOrganizerTheme {
                SportOrganizerApp()
            }
        }
    }
}

@Composable
fun SportOrganizerApp() {
    var isAuthenticated by remember { mutableStateOf(false) }
    var isCheckingSession by remember { mutableStateOf(true) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        try {
            isAuthenticated =
                com.sportorganizer.app.data.remote.supabase.auth
                    .currentSessionOrNull() != null
        } catch (_: Exception) {
            isAuthenticated = false
        } finally {
            isCheckingSession = false
        }
    }

    when {
        isCheckingSession -> {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        }

        !isAuthenticated -> {
            AuthScreen(
                onAuthenticated = {
                    isAuthenticated = true
                }
            )
        }

        else -> {
            SportOrganizerMainApp(
                onSignOut = {
                    isAuthenticated = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SportOrganizerMainApp(
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val scope = rememberCoroutineScope()

    val screens = listOf(
        Screen.Dashboard,
        Screen.Teams,
        Screen.Matches,
        Screen.Tournaments,
        Screen.Profile
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SPORT ORGANIZER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = currentDestination?.route
                                ?.let { route ->
                                    screens.find { it.route == route }?.title
                                } ?: "Sport Organizer",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Text(
                                text = when (screen) {
                                    Screen.Dashboard -> "H"
                                    Screen.Teams -> "T"
                                    Screen.Matches -> "M"
                                    Screen.Tournaments -> "C"
                                    Screen.Profile -> "P"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        },
                        label = {
                            Text(
                                text = when (screen) {
                                    Screen.Dashboard -> "Home"
                                    Screen.Teams -> "Teams"
                                    Screen.Matches -> "Matches"
                                    Screen.Tournaments -> "Events"
                                    Screen.Profile -> "Profile"
                                }
                            )
                        },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SportOrganizerNavHost(
                navController = navController,
                onSignOut = {
                    scope.launch {
                        try {
                            AuthRepository.signOut()
                            onSignOut()
                        } catch (e: Exception) {
                            android.util.Log.e(
                                "SportAuth",
                                "Sign out failed",
                                e
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SportOrganizerHome() {
    DashboardScreen()
}
