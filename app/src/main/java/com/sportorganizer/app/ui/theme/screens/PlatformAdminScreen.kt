package com.sportorganizer.app.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportorganizer.app.data.remote.repository.PlatformAdminRepository
import com.sportorganizer.app.data.remote.repository.PlatformCollege
import kotlinx.coroutines.launch

@Composable
fun PlatformAdminScreen(onBack: () -> Unit) {
    var colleges by remember { mutableStateOf<List<PlatformCollege>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    var selectedCollege by remember { mutableStateOf<PlatformCollege?>(null) }
    var targetUserId by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun refresh() {
        scope.launch {
            isLoading = true
            error = null
            try {
                colleges = PlatformAdminRepository.getColleges()
            } catch (e: Exception) {
                error = e.message ?: "Couldn't load colleges. Check your connection and platform-admin access."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF312E81), Color(0xFF4F46E5), Color(0xFF0891B2)))).padding(20.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("PLATFORM CONTROL", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = .8f), letterSpacing = 1.8.sp)
                        TextButton(onClick = onBack) { Text("Back", color = Color.White) }
                    }
                    Text("Super Admin", style = MaterialTheme.typography.headlineLarge, color = Color.White)
                    Text("Manage colleges and appoint their first administrators.", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = .86f))
                    Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = .16f)) {
                        Text("SECURE · SUPABASE VERIFIED", Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
        Text("Manage colleges across Sport Organizer. All privileged actions are verified by Supabase.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        notice?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { refresh() }) { Text("Retry") }
        }
        if (isLoading) {
            CircularProgressIndicator()
            Text("Loading colleges…")
        } else {
            Text("Colleges (${colleges.size})", style = MaterialTheme.typography.titleMedium)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                items(colleges, key = { it.collegeId }) { college ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            Text(college.collegeName, style = MaterialTheme.typography.titleLarge)
                            val location = listOfNotNull(college.city?.takeIf { it.isNotBlank() }, college.state?.takeIf { it.isNotBlank() }).joinToString(", ")
                            if (location.isNotBlank()) Text(location, style = MaterialTheme.typography.bodySmall)
                            Surface(shape = RoundedCornerShape(50), color = if (college.isActive) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.errorContainer) {
                                Text(if (college.isActive) "ACTIVE" else "INACTIVE", Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = if (college.isActive) Color(0xFF166534) else MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.labelSmall)
                            }
                            Text("College admins: ${college.adminCount}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("ID: ${college.collegeId}", style = MaterialTheme.typography.bodySmall)
                            if (college.adminCount == 0L && college.isActive) {
                                TextButton(onClick = {
                                    selectedCollege = if (selectedCollege?.collegeId == college.collegeId) null else college
                                    targetUserId = ""
                                    notice = null
                                }) { Text(if (selectedCollege?.collegeId == college.collegeId) "Cancel assignment" else "Assign first admin") }
                            }
                            if (selectedCollege?.collegeId == college.collegeId) {
                                Text("Enter the registered user's Supabase Auth user ID. Do not enter their email.", style = MaterialTheme.typography.bodySmall)
                                OutlinedTextField(
                                    value = targetUserId,
                                    onValueChange = { targetUserId = it },
                                    label = { Text("User UUID") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                )
                                Button(
                                    enabled = !isSubmitting && targetUserId.trim().isNotEmpty(),
                                    onClick = {
                                        scope.launch {
                                            isSubmitting = true
                                            error = null
                                            notice = null
                                            try {
                                                PlatformAdminRepository.assignInitialCollegeAdmin(college.collegeId, targetUserId)
                                                notice = "First college admin assigned successfully."
                                                selectedCollege = null
                                                targetUserId = ""
                                                refresh()
                                            } catch (e: Exception) {
                                                error = e.message ?: "Assignment failed. Verify the user UUID and try again."
                                            } finally {
                                                isSubmitting = false
                                            }
                                        }
                                    }
                                ) { Text(if (isSubmitting) "Assigning…" else "Confirm assignment") }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            TextButton(onClick = { refresh() }) { Text("Refresh colleges") }
        }
    }
}
