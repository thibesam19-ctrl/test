package com.axiom.aicoach.ui.screens.progress

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.aicoach.ui.components.*
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ── Demo data ─────────────────────────────────────────────────────────────────

data class WeightEntry(val date: LocalDate, val kg: Float)

val demoWeightHistory = listOf(
    WeightEntry(LocalDate.now().minusDays(30), 82.4f),
    WeightEntry(LocalDate.now().minusDays(23), 81.8f),
    WeightEntry(LocalDate.now().minusDays(16), 81.1f),
    WeightEntry(LocalDate.now().minusDays(9), 79.8f),
    WeightEntry(LocalDate.now().minusDays(2), 78.2f),
)

data class AchievementBadgeData(val emoji: String, val label: String, val earned: Boolean)

val achievements = listOf(
    AchievementBadgeData("🔥", "7-Day Streak", true),
    AchievementBadgeData("💪", "First Workout", true),
    AchievementBadgeData("🥗", "Food Logger", true),
    AchievementBadgeData("⚖️", "5kg Lost", true),
    AchievementBadgeData("💧", "Hydration Hero", false),
    AchievementBadgeData("🏋️", "10 Workouts", false),
)

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
            // ── Stat row ──────────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(Spacing.lg))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                ) {
                    StatCard(
                        label = "Start Weight",
                        value = "82.4 kg",
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        label = "Current",
                        value = "78.2 kg",
                        color = colors.success,
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        label = "Lost",
                        value = "4.2 kg",
                        color = colors.primary,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(Spacing.xl))
            }

            // ── Transformation summary card ───────────────────────────────────
            item {
                AxiomCard(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(colors.success, Color(0xFF16A34A))
                                )
                            )
                            .padding(Spacing.xl),
                    ) {
                        Column {
                            Text(
                                text = "🎯 4.2 kg lost in 30 days",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                            Spacer(Modifier.height(Spacing.sm))
                            Text(
                                text = "On track for your goal",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(Spacing.xl))
            }

            // ── Navigation cards ──────────────────────────────────────────────
            item {
                ProgressNavCard(
                    emoji = "⚖️",
                    title = "Weight History",
                    subtitle = "Track your weight over time",
                    onClick = onWeightHistory,
                )
                Spacer(Modifier.height(Spacing.lg))
                ProgressNavCard(
                    emoji = "📏",
                    title = "Body Measurements",
                    subtitle = "Chest, waist, hip & more",
                    onClick = onBodyMeasurements,
                )
                Spacer(Modifier.height(Spacing.lg))
                ProgressNavCard(
                    emoji = "📸",
                    title = "Progress Photos",
                    subtitle = "Visual transformation timeline",
                    onClick = onPhotoGallery,
                )
                Spacer(Modifier.height(Spacing.xl))
            }

            // ── Achievements section ──────────────────────────────────────────
            item {
                SectionHeader(title = "Achievements")
                Spacer(Modifier.height(Spacing.md))
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                    userScrollEnabled = false,
                ) {
                    items(achievements.take(6)) { badge ->
                        AchievementBadge(badge)
                    }
                }
                Spacer(Modifier.height(Spacing.s80))
            }
        }
    }
}

@Composable
private fun ProgressNavCard(emoji: String, title: String, subtitle: String, onClick: () -> Unit) {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp,
            )
            Spacer(Modifier.width(Spacing.lg))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = colors.textMuted,
            )
        }
    }
}

@Composable
private fun AchievementBadge(badge: AchievementBadgeData) {
    val colors = AxiomTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (badge.earned) colors.primaryLight else colors.borderSubtle),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = badge.emoji,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = badge.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (badge.earned) colors.textPrimary else colors.textMuted,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

// ── Weight History ────────────────────────────────────────────────────────────

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
                Icon(Icons.Default.Add, contentDescription = "Log weight")
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
        ) {
            Spacer(Modifier.height(Spacing.lg))

            // ── Stats row with trend indicator ────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                StatCard(
                    label = "Start",
                    value = "82.4 kg",
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                // Current weight stat with trend arrow overlay
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(
                        label = "Current",
                        value = "78.2 kg",
                        color = colors.success,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    // Trend down arrow in top-right of card
                    Text(
                        text = "↓",
                        color = colors.success,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(Spacing.md),
                    )
                }
                StatCard(
                    label = "Change",
                    value = "-4.2 kg",
                    color = colors.success,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(Spacing.xl))
            Text(
                text = "Log History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(Spacing.lg))

            LazyColumn {
                items(demoWeightHistory.reversed()) { entry ->
                    WeightEntryRow(entry, formatter)
                    HorizontalDivider(color = colors.borderSubtle)
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
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false; newWeight = "" }) {
                    Text("Save", color = colors.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; newWeight = "" }) {
                    Text("Cancel", color = colors.textMuted)
                }
            },
            containerColor = colors.card,
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
        Text(
            text = entry.date.format(formatter),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )
        Text(
            text = "${entry.kg} kg",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
        )
    }
}

// ── Body Measurements ─────────────────────────────────────────────────────────

@Composable
fun BodyMeasurementsScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    var measurements by remember {
        mutableStateOf(
            mapOf(
                "Chest" to "98.0",
                "Waist" to "84.0",
                "Hips" to "96.0",
                "Left Arm" to "35.0",
                "Right Arm" to "35.5",
                "Left Thigh" to "56.0",
                "Right Thigh" to "56.5",
                "Neck" to "38.0",
            )
        )
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
            item {
                Spacer(Modifier.height(Spacing.lg))
                Text(
                    text = "Last measured: Apr 15",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
                Spacer(Modifier.height(Spacing.lg))
            }

            items(measurements.entries.toList()) { (label, value) ->
                MeasurementRow(
                    label = label,
                    value = value,
                    onUpdate = { measurements = measurements + (label to it) },
                )
                HorizontalDivider(color = colors.borderSubtle)
            }

            item {
                Spacer(Modifier.height(Spacing.xxxl))
                AxiomPrimaryButton(
                    text = "Save Measurements",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(Spacing.s80))
            }
        }
    }
}

@Composable
private fun MeasurementRow(label: String, value: String, onUpdate: (String) -> Unit) {
    val colors = AxiomTheme.colors
    var editing by remember { mutableStateOf(false) }
    var editText by remember(value) { mutableStateOf(value) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        if (editing) {
            OutlinedTextField(
                value = editText,
                onValueChange = { editText = it },
                modifier = Modifier.width(110.dp),
                singleLine = true,
                suffix = { Text("cm") },
                shape = Radius.md,
            )
            IconButton(onClick = { onUpdate(editText); editing = false }) {
                Icon(Icons.Default.Check, contentDescription = "Save", tint = colors.success)
            }
        } else {
            Text(
                text = "$value cm",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
            )
            IconButton(onClick = { editing = true }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = colors.textMuted,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

// ── Photo Gallery ─────────────────────────────────────────────────────────────

@Composable
fun PhotoGalleryScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    var photos by remember { mutableStateOf(listOf("Week 1", "Week 2", "Week 3", "Week 4")) }

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) photos = photos + "Week ${photos.size + 1}"
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
                Icon(Icons.Default.AddAPhoto, contentDescription = "Add photo")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
        ) {
            // ── Privacy banner ────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(Spacing.lg))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Radius.lg)
                        .border(
                            width = 4.dp,
                            color = colors.info,
                            shape = Radius.lg,
                        )
                        .background(colors.card)
                        .padding(Spacing.xl),
                ) {
                    Text(
                        text = "🔒 Photos stored locally. Never uploaded without permission.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                    )
                }
                Spacer(Modifier.height(Spacing.xl))
            }

            // ── Photo grid ────────────────────────────────────────────────────
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                    userScrollEnabled = false,
                ) {
                    items(photos) { weekLabel ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(0.75f)
                                .clip(Radius.lg)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            colors.primaryLight,
                                            colors.primary.copy(alpha = 0.3f),
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📷", fontSize = 32.sp)
                                Spacer(Modifier.height(Spacing.md))
                                Text(
                                    text = weekLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(Spacing.s80))
            }
        }
    }
}
