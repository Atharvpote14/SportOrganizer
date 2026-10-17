
package com.sportorganizer.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportorganizer.app.auth.AuthViewModel

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var showSignUp by remember { mutableStateOf(false) }

    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.checkExistingSession()
    }

    LaunchedEffect(uiState.isAuthenticated, uiState.isLoading) {
        if (uiState.isAuthenticated && !uiState.isLoading) {
            onAuthenticated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Please wait...",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = if (showSignUp) "Create Account" else "Welcome Back",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display authentication errors and status messages
            uiState.message?.let { message ->
                Text(
                    text = message,
                    color = if (
                        message.contains("successfully", ignoreCase = true) ||
                        message.contains("account created", ignoreCase = true)
                    ) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (showSignUp) {
                SignUpScreen(
                    onSignUpClick = { name, email, password ->
                        authViewModel.signUp(name, email, password)
                    },
                    onLoginClick = {
                        showSignUp = false
                        authViewModel.clearMessage()
                    }
                )
            } else {
                LoginScreen(
                    onLoginClick = { email, password ->
                        authViewModel.signIn(email, password)
                    },
                    onSignUpClick = {
                        showSignUp = true
                        authViewModel.clearMessage()
                    }
                )
            }
        }
    }
}
