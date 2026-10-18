
package com.sportorganizer.app.ui.theme.screens

import com.sportorganizer.app.profile.RolePermissions
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportorganizer.app.profile.ProfileViewModel
import com.sportorganizer.app.data.remote.repository.PlatformAdminRepository

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    onOpenCollegeOnboarding: () -> Unit = {},
    onOpenAdminRequests: () -> Unit = {},
    onOpenPlatformAdmin: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel()
) {
    val state by profileViewModel.uiState.collectAsState()
    var isPlatformAdmin by remember { mutableStateOf(false) }
    var userIdCopied by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
        try {
            isPlatformAdmin = PlatformAdminRepository.isCurrentUserPlatformAdmin()
        } catch (_: Exception) {
            // Hide platform controls if role verification cannot be completed.
            isPlatformAdmin = false
        }
    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(
            onClick = onOpenCollegeOnboarding
        ) {
            Text("Find or Join a College")
        }
        if (isPlatformAdmin) {
            TextButton(onClick = onOpenPlatformAdmin) {
                Text("Platform Super Admin Dashboard")
            }
        }
        if (state.memberships.any { membership ->
                com.sportorganizer.app.profile.UserRole
                    .fromDatabase(membership.role) ==
                        com.sportorganizer.app.profile.UserRole.COLLEGE_ADMIN
            }) {
            TextButton(
                onClick = onOpenAdminRequests
            ) {
                Text("Admin Requests")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "My Profile",
                style = MaterialTheme.typography.headlineMedium
            )
            TextButton(onClick = onSignOut) {
                Text("Sign Out")
            }
        }

        when {
            state.isLoading -> {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator()
                Text("Loading your profile...")
            }

            state.error != null -> {
                Text(
                    text = state.error ?: "Something went wrong.",
                    color = MaterialTheme.colorScheme.error
                )
                TextButton(onClick = { profileViewModel.loadProfile() }) {
                    Text("Try Again")
                }
            }

            else -> {
                Text(
                    text = state.profile?.displayName
                        ?.takeIf { it.isNotBlank() }
                        ?: "Name not available",
                    style = MaterialTheme.typography.titleLarge
                )


                val currentUserId = state.profile?.id
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "User ID: ${currentUserId ?: "Profile not found"}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        enabled = !currentUserId.isNullOrBlank(),
                        onClick = {
                            currentUserId?.let { id ->
                                clipboardManager.setText(AnnotatedString(id))
                                userIdCopied = true
                            }
                        }
                    ) {
                        Text(if (userIdCopied) "Copied!" else "Copy ID")
                    }
                }
                if (userIdCopied) {
                    Text(
                        text = "User ID copied. Send it to the Platform Super Admin.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider()

                Text(
                    text = "My Permissions",
                    style = MaterialTheme.typography.titleMedium
                )

                if (state.memberships.isEmpty()) {
                    Text(
                        text = "No college role assigned yet. You can still view public live scores."
                    )
                } else {
                    state.memberships.forEach { membership ->
                        val role = com.sportorganizer.app.profile.UserRole
                            .fromDatabase(membership.role)

                        val college = state.availableColleges
                            .find { it.id == membership.collegeId }

                        Text(
                            text = college?.name ?: "College",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = "Role: ${role.displayName}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        val permissions = buildList {
                            if (RolePermissions.canManageCollege(role)) {
                                add("Manage college membership and settings")
                            }
                            if (RolePermissions.canManageTeams(role)) {
                                add("Manage teams")
                            }
                            if (RolePermissions.canManageTournaments(role)) {
                                add("Manage tournaments")
                            }
                            if (RolePermissions.canScoreMatches(role)) {
                                add("Score authorized matches")
                            }
                            add("View live scores")
                        }

                        permissions.forEach { permission ->
                            Text(
                                text = "• $permission",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        HorizontalDivider()
                    }
                }


                Text(
                    text = "My College Memberships",
                    style = MaterialTheme.typography.titleMedium
                )

                if (state.memberships.isEmpty()) {
                    Text("You don't have a college membership yet.")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.memberships,
                            key = { it.id }
                        ) { membership ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                val college = state.availableColleges.find {
                                    it.id == membership.collegeId
                                }

                                Text(
                                    text = college?.name ?: "College details unavailable",
                                    style = MaterialTheme.typography.titleSmall
                                )

                                Text(
                                    text = "Role: ${membership.role
                                        .replace('_', ' ')
                                        .split(' ')
                                        .joinToString(" ") { word ->
                                            word.replaceFirstChar { it.uppercase() }
                                        }}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                val location = college?.let {
                                    listOfNotNull(it.city, it.state).joinToString(", ")
                                }.orEmpty()

                                if (location.isNotBlank()) {
                                    Text(
                                        text = location,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                Text(
                    text = "Available Colleges",
                    style = MaterialTheme.typography.titleMedium
                )

                if (state.availableColleges.isEmpty()) {
                    Text("No active colleges found.")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = state.availableColleges,
                            key = { it.id }
                        ) { college ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = college.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                val location = listOfNotNull(
                                    college.city,
                                    college.state
                                ).joinToString(", ")

                                if (location.isNotBlank()) {
                                    Text(
                                        text = location,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
