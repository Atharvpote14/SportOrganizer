package com.sportorganizer.app.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportorganizer.app.data.remote.repository.PlatformAdminRepository
import com.sportorganizer.app.profile.ProfileViewModel
import com.sportorganizer.app.profile.RolePermissions
import com.sportorganizer.app.profile.UserRole

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
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
        try {
            isPlatformAdmin = PlatformAdminRepository.isCurrentUserPlatformAdmin()
        } catch (_: Exception) {
            // Hide platform controls if role verification cannot be completed.
            isPlatformAdmin = false
        }
    }

    val isCollegeAdmin = state.memberships.any {
        UserRole.fromDatabase(it.role) == UserRole.COLLEGE_ADMIN
    }

    // One single scroll container for the whole screen (fixes the broken scrolling).
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // ---------- My Profile header ----------
        Card(
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF3730A3), Color(0xFF4F46E5), Color(0xFF0891B2))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "MY PROFILE",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 1.8.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.16f),
                            onClick = onSignOut
                        ) {
                            Text(
                                "Sign Out",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    val displayName = state.profile?.displayName
                        ?.takeIf { it.isNotBlank() }
                        ?: "Name not available"

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    initialsOf(state.profile?.displayName),
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                displayName,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            val highestRole = state.roles.minByOrNull { it.ordinal }
                            val badge = when {
                                isPlatformAdmin -> "Platform Super Admin"
                                highestRole != null -> highestRole.displayName
                                else -> "Spectator"
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFD9F99D)
                            ) {
                                Text(
                                    badge.uppercase(),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    color = Color(0xFF17210D),
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CircularProgressIndicator(strokeWidth = 3.dp)
                    Text(
                        "Loading your profile...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            state.error != null -> {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "We couldn't load your profile",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = state.error ?: "Something went wrong.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Button(
                            onClick = { profileViewModel.loadProfile() },
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Try Again") }
                    }
                }
            }

            else -> {
                // ---------- User ID ----------
                val currentUserId = state.profile?.id
                ProfileCard {
                    Text(
                        "USER ID",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.5.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = currentUserId ?: "Profile not found",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            enabled = !currentUserId.isNullOrBlank(),
                            shape = RoundedCornerShape(14.dp),
                            onClick = {
                                currentUserId?.let { id ->
                                    clipboardManager.setText(AnnotatedString(id))
                                    userIdCopied = true
                                }
                            }
                        ) {
                            Text(if (userIdCopied) "Copied ✓" else "Copy ID")
                        }
                    }
                    if (userIdCopied) {
                        Text(
                            text = "User ID copied. Send it to the Platform Super Admin.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // ---------- Quick actions ----------
                ProfileSectionHeading("Quick actions", "Manage the spaces you belong to")
                ActionTile(
                    symbol = "🎓",
                    tint = Color(0xFFE0E7FF),
                    title = "Find or Join a College",
                    subtitle = "Request access to your college sports space",
                    onClick = onOpenCollegeOnboarding
                )
                if (isCollegeAdmin) {
                    ActionTile(
                        symbol = "📥",
                        tint = Color(0xFFD9FBEA),
                        title = "Admin Requests",
                        subtitle = "Review people asking to join your college",
                        onClick = onOpenAdminRequests
                    )
                }
                if (isPlatformAdmin) {
                    ActionTile(
                        symbol = "🛡",
                        tint = Color(0xFFFFE8CC),
                        title = "Platform Super Admin Dashboard",
                        subtitle = "Manage colleges and platform access",
                        onClick = onOpenPlatformAdmin
                    )
                }

                // ---------- Memberships + permissions ----------
                ProfileSectionHeading(
                    "My College Memberships",
                    "Your roles and what you're allowed to do"
                )
                if (state.memberships.isEmpty()) {
                    EmptyHint("No college role assigned yet. You can still view public live scores.")
                } else {
                    state.memberships.forEach { membership ->
                        val role = UserRole.fromDatabase(membership.role)
                        val college = state.availableColleges.find { it.id == membership.collegeId }
                        val location = college?.let {
                            listOfNotNull(it.city, it.state).joinToString(", ")
                        }.orEmpty()
                        val permissions = buildList {
                            if (RolePermissions.canManageCollege(role)) add("Manage college membership and settings")
                            if (RolePermissions.canManageTeams(role)) add("Manage teams")
                            if (RolePermissions.canManageTournaments(role)) add("Manage tournaments")
                            if (RolePermissions.canScoreMatches(role)) add("Score authorized matches")
                            add("View live scores")
                        }

                        ProfileCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            (college?.shortName ?: college?.name ?: "C")
                                                .take(1).uppercase(),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        college?.name ?: "College details unavailable",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (location.isNotBlank()) {
                                        Text(
                                            location,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        role.displayName,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "PERMISSIONS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.2.sp
                            )
                            permissions.forEach { permission ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFDCFCE7),
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                "✓",
                                                color = Color(0xFF15803D),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(permission, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }

                // ---------- Available colleges ----------
                ProfileSectionHeading(
                    "Available Colleges",
                    "Active colleges on Sport Organizer"
                )
                if (state.availableColleges.isEmpty()) {
                    EmptyHint("No active colleges found.")
                } else {
                    ProfileCard(contentSpacing = 0.dp) {
                        state.availableColleges.forEachIndexed { index, college ->
                            val location = listOfNotNull(college.city, college.state)
                                .joinToString(", ")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            college.name.take(1).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(college.name, style = MaterialTheme.typography.bodyLarge)
                                    if (location.isNotBlank()) {
                                        Text(
                                            location,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            if (index != state.availableColleges.lastIndex) {
                                androidx.compose.material3.HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Breathing room so the last card never hides behind the bottom bar.
        Spacer(Modifier.height(16.dp))
    }
}

private fun initialsOf(name: String?): String {
    val parts = name.orEmpty().trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> "${parts.first().first().uppercaseChar()}${parts.last().first().uppercaseChar()}"
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> "U"
    }
}

@Composable
private fun ProfileSectionHeading(title: String, subtitle: String) {
    Column(
        modifier = Modifier.padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileCard(
    contentSpacing: androidx.compose.ui.unit.Dp = 10.dp,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(contentSpacing),
            content = content
        )
    }
}

@Composable
private fun EmptyHint(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActionTile(
    symbol: String,
    tint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(15.dp),
                color = tint,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) { Text(symbol, fontSize = 22.sp) }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text("›", fontSize = 26.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}
