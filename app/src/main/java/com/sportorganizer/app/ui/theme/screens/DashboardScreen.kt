package com.sportorganizer.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportorganizer.app.data.remote.fetchConnectionTest
import kotlinx.coroutines.CancellationException
import com.sportorganizer.app.data.remote.repository.CollegeRepository

@Composable
fun DashboardScreen(onOpenProfile: () -> Unit = {}) {
    var userInitials by remember { mutableStateOf("U") }
    var connectionMessage by remember { mutableStateOf("Checking your secure connection…") }
    var connectionSuccess by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val profile = CollegeRepository.getCurrentProfile()
            val fullName = profile?.displayName.orEmpty().trim()
            val parts = fullName.split(Regex("\\s+")).filter { it.isNotBlank() }
            userInitials = when {
                parts.size >= 2 -> "${parts.first().first().uppercaseChar()}${parts.last().first().uppercaseChar()}"
                parts.size == 1 -> parts.first().take(2).uppercase()
                else -> "U"
            }
        } catch (_: Exception) {
            userInitials = "U"
        }
        try {
            val result = fetchConnectionTest()
            connectionMessage = result.firstOrNull()?.message ?: "Connected — service is responding."
            connectionSuccess = result.isNotEmpty()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            connectionMessage = "Could not verify connection. Pull-to-retry will be added with live data."
            connectionSuccess = false
        }
        visible = true
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AnimatedVisibility(visible = visible, enter = fadeIn() + slideInVertically(initialOffsetY = { it / 8 })) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("YOUR SPORTS HUB", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, letterSpacing = 1.8.sp)
                        Text("Game on.", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onBackground)
                        Text("Everything your sports community needs.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(54.dp).clickable(onClick = onOpenProfile),
                        tonalElevation = 4.dp,
                        shadowElevation = 3.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(userInitials, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                HeroCard()

                Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(shape = CircleShape, color = if (connectionSuccess) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.size(40.dp)) {
                            Box(contentAlignment = Alignment.Center) { Text(if (connectionSuccess) "✓" else "↻", color = if (connectionSuccess) Color(0xFF15803D) else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(if (connectionSuccess) "Systems operational" else "Connection status", style = MaterialTheme.typography.titleSmall)
                            Text(connectionMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(shape = RoundedCornerShape(50), color = if (connectionSuccess) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant) {
                            Text(if (connectionSuccess) "ONLINE" else "CHECK", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = if (connectionSuccess) Color(0xFF166534) else MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                SectionHeading("Your overview", "A quick look at your sports space")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricCard("Tournaments", "—", "Competitions", "🏆", Modifier.weight(1f))
                    MetricCard("Teams", "—", "Registered squads", "⚡", Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricCard("Matches", "—", "Fixtures planned", "🏟", Modifier.weight(1f))
                    MetricCard("Results", "—", "Games completed", "📊", Modifier.weight(1f))
                }

                SectionHeading("Made for match day", "Tools for your next big game")
                ActionRow("01", "Create a tournament", "Build a competition and bring teams together", Color(0xFFE0E7FF))
                ActionRow("02", "Organize your teams", "Keep your squads and participants organized", Color(0xFFD9FBEA))
                ActionRow("03", "Follow live matches", "Scores, fixtures and results in one place", Color(0xFFFFE8CC))
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun HeroCard() {
    Card(shape = RoundedCornerShape(28.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF3730A3), Color(0xFF4F46E5), Color(0xFF0891B2)))).padding(24.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(13.dp), modifier = Modifier.fillMaxWidth()) {
                Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.16f)) {
                    Text("✦  BUILT FOR THE LOVE OF SPORT", modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = Color.White, style = MaterialTheme.typography.labelSmall, letterSpacing = 1.sp)
                }
                Text("Every game.\nOne platform.", style = MaterialTheme.typography.headlineLarge, color = Color.White, lineHeight = 34.sp)
                Text("From the first fixture to the final whistle — keep your sports community moving.", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.86f))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFD9F99D)) { Text("LET'S PLAY  →", modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), color = Color(0xFF17210D), style = MaterialTheme.typography.labelLarge) }
                    Text("SPORT ORGANIZER", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.72f), letterSpacing = 1.sp)
                }
            }
        }
    }
}

@Composable
private fun SectionHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun MetricCard(title: String, value: String, subtitle: String, symbol: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(symbol, fontSize = 19.sp)
            }
            Text(value, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Box(Modifier.fillMaxWidth().height(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))) {
                val progress by animateFloatAsState(targetValue = 0.62f, label = "metricAccent")
                Box(Modifier.fillMaxWidth(progress).height(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
            }
        }
    }
}

@Composable
private fun ActionRow(number: String, title: String, subtitle: String, tint: Color) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(shape = RoundedCornerShape(15.dp), color = tint, modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) { Text(number, style = MaterialTheme.typography.titleMedium, color = Color(0xFF20243A), fontWeight = FontWeight.Bold) }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", fontSize = 28.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}
