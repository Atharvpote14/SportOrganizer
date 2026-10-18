
package com.sportorganizer.app


import com.sportorganizer.app.auth.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.jan.supabase.auth.auth
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sportorganizer.app.data.remote.repository.AuthRepository
import com.sportorganizer.app.data.remote.supabase
import com.sportorganizer.app.ui.screens.AuthScreen
import com.sportorganizer.app.ui.screens.DashboardScreen
import com.sportorganizer.app.ui.theme.SportOrganizerTheme
import com.sportorganizer.app.ui.theme.navigation.Screen
import com.sportorganizer.app.ui.theme.navigation.SportOrganizerNavHost
import kotlinx.coroutines.launch

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
    val authViewModel: AuthViewModel = viewModel()
    var isAuthenticated by remember { mutableStateOf(false) }
    var isCheckingSession by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val auth = com.sportorganizer.app.data.remote.supabase.auth

            // Explicitly load the saved session before checking it.
            auth.loadFromStorage()

            isAuthenticated = auth.currentSessionOrNull() != null
        } catch (e: Exception) {
            android.util.Log.e(
                "SportAuth",
                "Failed to restore saved session",
                e
            )
            isAuthenticated = false
        } finally {
            isCheckingSession = false
        }
    }

    when {
        isCheckingSession -> {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.linearGradient(listOf(Color(0xFF10152B), Color(0xFF312E81), Color(0xFF155E75)))
                ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.13f), modifier = Modifier.size(92.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("S", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    Text("SPORT ORGANIZER", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.8.sp)
                    Text("Every game. One platform.", color = Color.White.copy(alpha = 0.75f), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(2.dp))
                    CircularProgressIndicator(color = Color(0xFFD9F99D), strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                }
            }
        }

        !isAuthenticated -> {
            AuthScreen(
                onAuthenticated = {
                    isAuthenticated = true
                },
                authViewModel = authViewModel
            )
        }

        else -> {
            SportOrganizerMainApp(
                onSignOut = {
                    authViewModel.resetAuthenticationState()
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
    var isSigningOut by remember { mutableStateOf(false) }

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
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
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
                                    Screen.CollegeOnboarding -> ""
                                    Screen.CollegeAdminRequests -> ""
                                    Screen.PlatformAdmin -> ""
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
                                    Screen.CollegeOnboarding -> "Join College"
                                    Screen.CollegeAdminRequests -> "Admin Requests"
                                    Screen.PlatformAdmin -> "Super Admin"
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
            if (isSigningOut) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                SportOrganizerNavHost(
                    navController = navController,
                    onSignOut = {
                        if (!isSigningOut) {
                            scope.launch {
                                isSigningOut = true

                                try {
                                    AuthRepository.signOut()

                                    // Change the app to the login screen only
                                    // after Supabase sign-out succeeds.
                                    onSignOut()
                                } catch (e: Exception) {
                                    Log.e(
                                        "SportAuth",
                                        "Sign out failed",
                                        e
                                    )
                                    isSigningOut = false
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SportOrganizerHome(onOpenProfile: () -> Unit = {}) {
    DashboardScreen(onOpenProfile = onOpenProfile)
}
