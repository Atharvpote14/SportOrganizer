
package com.sportorganizer.app.ui.theme.screens

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sportorganizer.app.data.remote.repository.CollegeJoinRequest
import com.sportorganizer.app.data.remote.repository.CollegeJoinRequestRepository
import com.sportorganizer.app.data.remote.repository.CollegeRepository
import kotlinx.coroutines.launch

private data class AdminRequestItem(
    val request: CollegeJoinRequest,
    val collegeName: String
)

@Composable
fun CollegeAdminRequestsScreen(
    onBack: () -> Unit = {}
) {
    var requests by remember { mutableStateOf<List<AdminRequestItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var busyRequestId by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    suspend fun loadRequests() {
        isLoading = true
        message = null

        try {
            val pendingRequests =
                CollegeJoinRequestRepository.getPendingRequestsForMyColleges()

            val colleges = CollegeRepository.getActiveColleges()
            requests = pendingRequests.map { request ->
                AdminRequestItem(
                    request = request,
                    collegeName = colleges.firstOrNull {
                        it.id == request.collegeId
                    }?.name ?: "Unknown college"
                )
            }
        } catch (e: Exception) {
            message = e.message ?: "Unable to load requests."
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadRequests()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "College Join Requests",
                style = MaterialTheme.typography.titleLarge
            )
            OutlinedButton(onClick = onBack) {
                Text("Back")
            }
        }

        OutlinedButton(
            onClick = { scope.launch { loadRequests() } },
            enabled = !isLoading && busyRequestId == null
        ) {
            Text("Refresh")
        }

        message?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        when {
            isLoading -> {
                CircularProgressIndicator()
            }

            requests.isEmpty() -> {
                Text("No pending join requests.")
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = requests,
                        key = { it.request.id }
                    ) { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = item.collegeName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("User ID: ${item.request.userId}")
                            Text("Status: ${item.request.status}")

                            item.request.message
                                ?.takeIf { it.isNotBlank() }
                                ?.let {
                                    Text("Message: $it")
                                }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            busyRequestId = item.request.id
                                            message = null
                                            try {
                                                CollegeJoinRequestRepository
                                                    .approveRequest(item.request.id)
                                                message = "Request approved."
                                                loadRequests()
                                            } catch (e: Exception) {
                                                message = e.message
                                                    ?: "Unable to approve request."
                                            } finally {
                                                busyRequestId = null
                                            }
                                        }
                                    },
                                    enabled = busyRequestId == null
                                ) {
                                    Text(
                                        if (busyRequestId == item.request.id)
                                            "Please wait..."
                                        else
                                            "Approve"
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            busyRequestId = item.request.id
                                            message = null
                                            try {
                                                CollegeJoinRequestRepository
                                                    .rejectRequest(item.request.id)
                                                message = "Request rejected."
                                                loadRequests()
                                            } catch (e: Exception) {
                                                message = e.message
                                                    ?: "Unable to reject request."
                                            } finally {
                                                busyRequestId = null
                                            }
                                        }
                                    },
                                    enabled = busyRequestId == null
                                ) {
                                    Text("Reject")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}
