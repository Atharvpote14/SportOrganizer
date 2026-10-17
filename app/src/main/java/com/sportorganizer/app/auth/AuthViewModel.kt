
package com.sportorganizer.app.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportorganizer.app.data.remote.repository.AuthRepository
import com.sportorganizer.app.data.remote.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun checkExistingSession() {
        // Reset the old state immediately, before launching the coroutine.
        // This prevents a stale authenticated state from reopening the dashboard.
        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val hasSession =
                    supabase.auth.currentSessionOrNull() != null

                _uiState.value = AuthUiState(
                    isAuthenticated = hasSession
                )
            } catch (e: Exception) {
                Log.e("SportAuth", "Session check failed", e)

                _uiState.value = AuthUiState(
                    message = "Unable to check your session. Please try again."
                )
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            try {
                AuthRepository.signIn(email, password)

                val hasSession =
                    supabase.auth.currentSessionOrNull() != null

                _uiState.value = if (hasSession) {
                    AuthUiState(
                        isAuthenticated = true,
                        message = "Signed in successfully."
                    )
                } else {
                    AuthUiState(
                        message = "Sign-in completed, but no active session was found."
                    )
                }

            } catch (e: Exception) {
                Log.e("SportAuth", "Sign-in failed", e)

                val errorMessage = e.message.orEmpty()

                val friendlyMessage = when {
                    errorMessage.contains(
                        "email_not_confirmed",
                        ignoreCase = true
                    ) ->
                        "Please verify your email before signing in."

                    errorMessage.contains(
                        "invalid_credentials",
                        ignoreCase = true
                    ) ||
                            errorMessage.contains(
                                "invalid login credentials",
                                ignoreCase = true
                            ) ->
                        "Incorrect email or password. Please try again."

                    errorMessage.contains(
                        "network",
                        ignoreCase = true
                    ) ->
                        "Connection problem. Check your internet and try again."

                    else ->
                        "Sign-in failed. Please check your details and try again."
                }

                _uiState.value = AuthUiState(
                    isLoading = false,
                    isAuthenticated = false,
                    message = friendlyMessage
                )
            }
        }
    }

    fun signUp(
        fullName: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            try {
                AuthRepository.signUp(fullName, email, password)

                val hasSession =
                    supabase.auth.currentSessionOrNull() != null

                _uiState.value = if (hasSession) {
                    AuthUiState(
                        isAuthenticated = true,
                        message = "Account created successfully."
                    )
                } else {
                    AuthUiState(
                        message = "Account created. Please check your email to verify your account, then sign in."
                    )
                }

            } catch (e: Exception) {
                Log.e("SportAuth", "Sign-up failed", e)

                val errorMessage = e.message.orEmpty()

                val friendlyMessage = when {
                    errorMessage.contains(
                        "already registered",
                        ignoreCase = true
                    ) ->
                        "This email is already registered. Please sign in instead."

                    errorMessage.contains(
                        "network",
                        ignoreCase = true
                    ) ->
                        "Connection problem. Check your internet and try again."

                    else ->
                        "Account creation failed. Please check your details and try again."
                }

                _uiState.value = AuthUiState(
                    message = friendlyMessage
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
