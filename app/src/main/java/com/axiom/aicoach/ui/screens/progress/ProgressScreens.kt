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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.aicoach.ai.vision.body.BodyAnalysisResult
import com.axiom.aicoach.ai.vision.body.InsightSentiment
import com.axiom.aicoach.ui.components.*
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing

// ── Static achievement data (not yet backed by a ViewModel) ──────────────────

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
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val colors = AxiomTheme.colors
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val startWeightLabel = uiState.startWeightKg?.let { String.format("%.1f kg", it) } ?: "—"
    val currentWeightLabel = uiState.currentWeightKg?.let { String.format("%.1f kg", it) } ?: "—"
    val lostLabel = if (uiState.totalWeightLost != 0f) {
        String.format("%.1f kg", uiState.totalWeightLost)
    } else "—"
    val summaryText = if (uiState.totalWeightLost > 0f) {
        "🎯 ${String.format("%.1f", uiState.totalWeightLost)} kg lost — on track for your goal"
    } else {
        "🎯 Log your first weight to start tracking"
    }

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
                        value = startWeightLabel,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        label = "Current",
                        value = currentWeightLabel,
                        color = colors.success,
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        label = "Lost",
                        value = lostLabel,
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
                                text = summaryText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                            Spacer(Modifier.height(Spacing.sm))
                            Text(
                                text = "Keep up the great work",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(Spacing.xl))
            }

            // ── AI Body Analysis card ─────────────────────────────────────────
            item {
                uiState.bodyAnalysis?.let { analysis ->
                    BodyAnalysisCard(analysis)
                    Spacer(Modifier.height(Spacing.xl))
                }
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
fun WeightHistoryScreen(
    onBack: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val colors = AxiomTheme.colors
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var newWeight by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val startLabel = uiState.startWeightKg?.let { String.format("%.1f kg", it) } ?: "—"
    val currentLabel = uiState.currentWeightKg?.let { String.format("%.1f kg", it) } ?: "—"
    val changeLabel = if (uiState.totalWeightLost != 0f) {
        String.format("%.1f kg", -uiState.totalWeightLost)
    } else "—"

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
                    value = startLabel,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                // Current weight stat with trend arrow overlay
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(
                        label = "Current",
                        value = currentLabel,
                        color = colors.success,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (uiState.totalWeightLost > 0f) {
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
                }
                StatCard(
                    label = "Change",
                    value = changeLabel,
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

            if (uiState.weightLogs.isEmpty()) {
                EmptyState(
                    title = "No weight logs yet",
                    message = "Tap + to log your first weight",
                )
            } else {
                LazyColumn {
                    items(uiState.weightLogs) { entry ->
                        WeightLogEntryRow(
                            entry = entry,
                            onDelete = { viewModel.deleteWeight(entry.id) },
                        )
                        HorizontalDivider(color = colors.borderSubtle)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false; newWeight = "" },
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
                TextButton(
                    onClick = {
                        val kg = newWeight.toFloatOrNull()
                        if (kg != null && kg > 0f) {
                            viewModel.logWeight(kg)
                        }
                        showDialog = false
                        newWeight = ""
                    }
                ) {
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
private fun WeightLogEntryRow(entry: WeightLogUi, onDelete: () -> Unit) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = entry.date,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )
        Text(
            text = "${entry.weightKg} kg",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
        )
        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, null, tint = colors.textMuted, modifier = Modifier.size(16.dp))
        }
    }
}

// ── Body Measurements ─────────────────────────────────────────────────────────

@Composable
fun BodyMeasurementsScreen(
    onBack: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val colors = AxiomTheme.colors
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Editable state initialised from the latest measurement (if any)
    val latest = uiState.bodyMeasurements.firstOrNull()

    var chest by remember(latest) { mutableStateOf(latest?.chestCm?.let { String.format("%.1f", it) } ?: "") }
    var waist by remember(latest) { mutableStateOf(latest?.waistCm?.let { String.format("%.1f", it) } ?: "") }
    var hips by remember(latest) { mutableStateOf(latest?.hipCm?.let { String.format("%.1f", it) } ?: "") }
    var arm by remember(latest) { mutableStateOf(latest?.armCm?.let { String.format("%.1f", it) } ?: "") }
    var thigh by remember(latest) { mutableStateOf(latest?.thighCm?.let { String.format("%.1f", it) } ?: "") }

    val lastMeasuredLabel = latest?.date?.let { "Last measured: $it" } ?: "No measurements yet"

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
                    text = lastMeasuredLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
                Spacer(Modifier.height(Spacing.lg))
            }

            item {
                MeasurementRow(label = "Chest", value = chest, onUpdate = { chest = it })
                HorizontalDivider(color = colors.borderSubtle)
                MeasurementRow(label = "Waist", value = waist, onUpdate = { waist = it })
                HorizontalDivider(color = colors.borderSubtle)
                MeasurementRow(label = "Hips", value = hips, onUpdate = { hips = it })
                HorizontalDivider(color = colors.borderSubtle)
                MeasurementRow(label = "Arm", value = arm, onUpdate = { arm = it })
                HorizontalDivider(color = colors.borderSubtle)
                MeasurementRow(label = "Thigh", value = thigh, onUpdate = { thigh = it })
                HorizontalDivider(color = colors.borderSubtle)
            }

            item {
                Spacer(Modifier.height(Spacing.xxxl))
                AxiomPrimaryButton(
                    text = "Save Measurements",
                    onClick = {
                        viewModel.saveMeasurement(
                            waist = waist.toFloatOrNull(),
                            hip = hips.toFloatOrNull(),
                            chest = chest.toFloatOrNull(),
                            arm = arm.toFloatOrNull(),
                            thigh = thigh.toFloatOrNull(),
                        )
                    },
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
                text = if (value.isNotBlank()) "$value cm" else "—",
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
fun PhotoGalleryScreen(
    onBack: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val colors = AxiomTheme.colors
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Local list merges DB photos + any newly picked in-session
    var localPhotos by remember { mutableStateOf(listOf<String>()) }

    val allPhotoLabels = uiState.progressPhotos.map { it.date } + localPhotos

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) localPhotos = localPhotos + "New Photo"
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
                if (allPhotoLabels.isEmpty()) {
                    EmptyState(
                        title = "No photos yet",
                        message = "Tap the camera button to add your first progress photo",
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                        userScrollEnabled = false,
                    ) {
                        items(allPhotoLabels) { label ->
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
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(Spacing.s80))
            }
        }
    }
}

@Composable
private fun BodyAnalysisCard(analysis: BodyAnalysisResult) {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🤖", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(Spacing.md))
                Column {
                    Text(
                        "AI Body Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                    )
                    Text(
                        analysis.disclaimer,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                }
            }
            Spacer(Modifier.height(Spacing.xl))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                AnalysisScorePill("Transformation", analysis.transformationScore, colors.primary)
                AnalysisScorePill("Consistency", analysis.consistencyScore, colors.success)
            }
            if (analysis.insights.isNotEmpty()) {
                Spacer(Modifier.height(Spacing.xl))
                analysis.insights.take(3).forEach { insight ->
                    val emoji = when (insight.sentiment) {
                        InsightSentiment.POSITIVE    -> "✅"
                        InsightSentiment.ENCOURAGING -> "💪"
                        InsightSentiment.NEUTRAL     -> "ℹ️"
                    }
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(emoji, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.width(Spacing.md))
                        Text(
                            insight.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysisScorePill(label: String, score: Int, color: androidx.compose.ui.graphics.Color) {
    val colors = AxiomTheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "$score",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            "/100",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
        )
    }
}
