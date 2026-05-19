package com.axiom.aicoach.ui.screens.progress

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.components.*
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ── Progress Overview ─────────────────────────────────────────────────────────

@Composable
fun ProgressOverviewScreen(
    onWeightHistory: () -> Unit,
    onBodyMeasurements: () -> Unit,
    onPhotoGallery: () -> Unit,
    onBack: () -> Unit,
) {
    val colors = AxiomTheme.colors
    Scaffold(
        topBar = { AxiomTopBar("Progress", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
        ) {
            item {
                Spacer(Modifier.height(Spacing.md))
                // Summary cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard("Start Weight", "82.4 kg", modifier = Modifier.weight(1f))
                    StatCard("Current", "78.2 kg", color = colors.success, modifier = Modifier.weight(1f))
                    StatCard("Lost", "4.2 kg", color = colors.primary, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(Spacing.xl))
            }

            item {
                ProgressNavCard("⚖️ Weight History", "Track your weight over time", onWeightHistory)
                Spacer(Modifier.height(Spacing.lg))
                ProgressNavCard("📏 Body Measurements", "Chest, waist, hip & more", onBodyMeasurements)
                Spacer(Modifier.height(Spacing.lg))
                ProgressNavCard("📸 Progress Photos", "Visual transformation timeline", onPhotoGallery)
                Spacer(Modifier.height(Spacing.xl))
            }

            item {
                Text("Achievements", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(Spacing.lg))
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(achievements.take(6)) { badge ->
                        AchievementBadge(badge)
                    }
                }
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ProgressNavCard(emoji: String, subtitle: String, onClick: () -> Unit) {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.width(Spacing.lg))
                Column {
                    Text(emoji.substringAfter(" "), style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = colors.textMuted)
        }
    }
}

data class AchievementBadgeData(val emoji: String, val label: String, val earned: Boolean)

val achievements = listOf(
    AchievementBadgeData("🔥", "7-Day Streak", true),
    AchievementBadgeData("💪", "First Workout", true),
    AchievementBadgeData("🥗", "Food Logger", true),
    AchievementBadgeData("⚖️", "5kg Lost", true),
    AchievementBadgeData("💧", "Hydration Hero", false),
    AchievementBadgeData("🏋️", "10 Workouts", false),
)

@Composable
private fun AchievementBadge(badge: AchievementBadgeData) {
    val colors = AxiomTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp),
    ) {
        Box(
            Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (badge.earned) colors.primaryLight else colors.borderSubtle),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                badge.emoji,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.then(if (!badge.earned) Modifier.then(Modifier) else Modifier),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            badge.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (badge.earned) colors.textPrimary else colors.disabledText,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

// ── Weight History ────────────────────────────────────────────────────────────

data class WeightEntry(val date: LocalDate, val kg: Float)

val demoWeightHistory = listOf(
    WeightEntry(LocalDate.now().minusDays(30), 82.4f),
    WeightEntry(LocalDate.now().minusDays(23), 81.8f),
    WeightEntry(LocalDate.now().minusDays(16), 81.1f),
    WeightEntry(LocalDate.now().minusDays(9), 79.8f),
    WeightEntry(LocalDate.now().minusDays(2), 78.2f),
)

@Composable
fun WeightHistoryScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    var newWeight by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("MMM d")

    Scaffold(
        topBar = { AxiomTopBar("Weight History", onBack = onBack) },
        containerColor = colors.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = colors.primary,
                contentColor = colors.textOnPrimary,
            ) {
                Icon(Icons.Default.Add, "Log weight")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
        ) {
            Spacer(Modifier.height(Spacing.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard("Start", "82.4 kg", modifier = Modifier.weight(1f))
                StatCard("Current", "78.2 kg", color = colors.success, modifier = Modifier.weight(1f))
                StatCard("Change", "-4.2 kg", color = colors.success, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(Spacing.xl))
            Text("Log History", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(Spacing.lg))
            LazyColumn {
                items(demoWeightHistory.reversed()) { entry ->
                    WeightEntryRow(entry, formatter)
                    Divider(color = colors.borderSubtle)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Log Weight") },
            text = {
                OutlinedTextField(
                    value = newWeight,
                    onValueChange = { newWeight = it },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    suffix = { Text("kg") },
                    shape = Radius.md,
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun WeightEntryRow(entry: WeightEntry, formatter: DateTimeFormatter) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(entry.date.format(formatter), style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
        Text("${entry.kg} kg", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
    }
}

// ── Body Measurements ─────────────────────────────────────────────────────────

@Composable
fun BodyMeasurementsScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    var measurements by remember {
        mutableStateOf(mapOf(
            "Chest" to "98.0",
            "Waist" to "84.0",
            "Hips" to "96.0",
            "Left Arm" to "35.0",
            "Right Arm" to "35.5",
            "Left Thigh" to "56.0",
            "Right Thigh" to "56.5",
            "Neck" to "38.0",
        ))
    }

    Scaffold(
        topBar = { AxiomTopBar("Body Measurements", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
        ) {
            item { Spacer(Modifier.height(Spacing.md)) }
            items(measurements.entries.toList()) { (label, value) ->
                MeasurementRow(label, value, onUpdate = { measurements = measurements + (label to it) })
                Divider(color = colors.borderSubtle)
            }
            item {
                Spacer(Modifier.height(Spacing.xxxl))
                AxiomPrimaryButton("Save Measurements", onClick = {}, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun MeasurementRow(label: String, value: String, onUpdate: (String) -> Unit) {
    val colors = AxiomTheme.colors
    var editing by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary, modifier = Modifier.weight(1f))
        if (editing) {
            var text by remember { mutableStateOf(value) }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.width(100.dp),
                singleLine = true,
                suffix = { Text("cm") },
                shape = Radius.md,
            )
            IconButton(onClick = { onUpdate(text); editing = false }) {
                Icon(Icons.Default.Check, null, tint = colors.success)
            }
        } else {
            Text("${value} cm", style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
            IconButton(onClick = { editing = true }) {
                Icon(Icons.Default.Edit, null, tint = colors.textMuted, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ── Photo Gallery ─────────────────────────────────────────────────────────────

@Composable
fun PhotoGalleryScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    var photos by remember { mutableStateOf(listOf("📸 Week 1", "📸 Week 2", "📸 Week 3", "📸 Week 4")) }

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) photos = photos + "📸 Week ${photos.size + 1}"
    }

    Scaffold(
        topBar = { AxiomTopBar("Progress Photos", onBack = onBack) },
        containerColor = colors.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { photoLauncher.launch("image/*") },
                containerColor = colors.primary,
                contentColor = colors.textOnPrimary,
            ) {
                Icon(Icons.Default.AddAPhoto, "Add photo")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
        ) {
            Spacer(Modifier.height(Spacing.md))
            Text("Privacy note: photos are stored locally on your device and never uploaded without your permission.",
                style = MaterialTheme.typography.bodySmall, color = colors.textMuted, modifier = Modifier.padding(bottom = Spacing.xl))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(photos) { photo ->
                    Box(
                        Modifier
                            .aspectRatio(0.75f)
                            .clip(Radius.lg)
                            .background(colors.primaryLight),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📷", style = MaterialTheme.typography.displaySmall)
                            Spacer(Modifier.height(8.dp))
                            Text(photo, style = MaterialTheme.typography.labelSmall, color = colors.primary)
                        }
                    }
                }
            }
        }
    }
}
