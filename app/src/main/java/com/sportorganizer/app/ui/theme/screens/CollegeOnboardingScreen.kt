
package com.sportorganizer.app.ui.theme.screens

import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportorganizer.app.data.remote.repository.College
import com.sportorganizer.app.data.remote.repository.CollegeJoinRequest
import com.sportorganizer.app.data.remote.repository.CollegeJoinRequestRepository
import com.sportorganizer.app.data.remote.repository.CollegeRepository
import kotlinx.coroutines.launch

@Composable
fun CollegeOnboardingScreen(
    onBack: () -> Unit = {},
    onboardingViewModel: CollegeOnboardingViewModel = viewModel()
) {
    val state = onboardingViewModel.state

    LaunchedEffect(Unit) {
        onboardingViewModel.load()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Join Your College",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Choose your college and request membership. "
                    + "An authorized college admin must approve your request.",
            style = MaterialTheme.typography.bodyMedium
        )

        TextButton(onClick = onBack) {
            Text("Back")
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            state.success?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
            }

            OutlinedTextField(
                value = state.message,
                onValueChange = onboardingViewModel::updateMessage,
                label = { Text("Message (optional)") },
                placeholder = { Text("e.g. I am a student at this college") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Text(
                text = "Available Colleges",
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.colleges, key = { it.id }) { college ->
                    val existingRequest = state.requests
                        .firstOrNull { it.collegeId == college.id }

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = college.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            val location = listOfNotNull(
                                college.city?.takeIf { it.isNotBlank() },
                                college.state?.takeIf { it.isNotBlank() }
                            ).joinToString(", ")

                            if (location.isNotBlank()) {
                                Text(location)
                            }

                            if (existingRequest != null) {
                                Text("Request status: ${existingRequest.status.uppercase()}")
                            }

                            Button(
                                onClick = {
                                    onboardingViewModel.submit(college.id)
                                },
                                enabled = !state.isSubmitting &&
                                        existingRequest == null
                            ) {
                                Text(
                                    if (state.isSubmitting) "Submitting..."
                                    else if (existingRequest != null) "Request Submitted"
                                    else "Request to Join"
                                )
                            }
                        }
                    }
                }
            }

            TextButton(
                onClick = onboardingViewModel::load,
                enabled = !state.isSubmitting
            ) {
                Text("Refresh")
            }
        }
    }
}

data class CollegeOnboardingUiState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val colleges: List<College> = emptyList(),
    val requests: List<CollegeJoinRequest> = emptyList(),
    val message: String = "",
    val error: String? = null,
    val success: String? = null
)

class CollegeOnboardingViewModel : ViewModel() {
    var state by mutableStateOf(CollegeOnboardingUiState())
        private set

    fun updateMessage(value: String) {
        state = state.copy(message = value, error = null, success = null)
    }

    fun load() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null,
                success = null
            )

            try {
                val colleges = CollegeRepository.getActiveColleges()
                val requests = CollegeJoinRequestRepository.getMyRequests()

                state = state.copy(
                    isLoading = false,
                    colleges = colleges,
                    requests = requests
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = e.message ?: "Unable to load colleges."
                )
            }
        }
    }

    fun submit(collegeId: String) {
        if (state.isSubmitting) return

        viewModelScope.launch {
            state = state.copy(
                isSubmitting = true,
                error = null,
                success = null
            )

            try {
                CollegeJoinRequestRepository.submitRequest(
                    collegeId = collegeId,
                    message = state.message
                )

                val requests = CollegeJoinRequestRepository.getMyRequests()

                state = state.copy(
                    isSubmitting = false,
                    requests = requests,
                    message = "",
                    success = "Your join request was submitted."
                )
            } catch (e: Exception) {
                state = state.copy(
                    isSubmitting = false,
                    error = e.message
                        ?: "Unable to submit your request."
                )
            }
        }
    }
}
