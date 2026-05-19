package com.axiom.aicoach.ui.screens.workout

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.aicoach.ai.vision.pose.ExerciseType
import com.axiom.aicoach.ai.vision.pose.FeedbackSeverity
import com.axiom.aicoach.ai.vision.pose.FormFeedback
import com.axiom.aicoach.ai.vision.pose.WorkoutFormViewModel
import com.axiom.aicoach.ui.components.AxiomCard
import com.axiom.aicoach.ui.components.AxiomTopBar
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WorkoutFormScreen(
    onBack: () -> Unit,
    viewModel: WorkoutFormViewModel = hiltViewModel(),
) {
    val colors = AxiomTheme.colors
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val repCount by viewModel.repCount.collectAsStateWithLifecycle()
    val formScore by viewModel.formScore.collectAsStateWithLifecycle()
    val feedback by viewModel.feedback.collectAsStateWithLifecycle()

    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(cameraPermission.status.isGranted) {
        if (cameraPermission.status.isGranted) viewModel.onPermissionGranted()
    }

    Scaffold(
        topBar = { AxiomTopBar("AI Form Analysis", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + Spacing.md,
                bottom = padding.calculateBottomPadding() + 24.dp,
                start = Spacing.xl,
                end = Spacing.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        ) {
            // Exercise selector
            item {
                Text(
                    "Select Exercise",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                )
                Spacer(Modifier.height(Spacing.lg))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    ExerciseType.values()
                        .filter { it != ExerciseType.UNKNOWN }
                        .forEach { type ->
                            ExerciseChip(
                                label = type.displayName(),
                                selected = uiState.exerciseType == type,
                                onClick = { viewModel.selectExercise(type) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                }
            }

            // Camera preview
            item {
                if (cameraPermission.status.isGranted) {
                    CameraAnalysisPreview(
                        onFrame = { imageProxy ->
                            viewModel.processFrame(imageProxy)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .clip(Radius.lg),
                    )
                } else {
                    // Permission prompt card
                    AxiomCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.xxxl),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(48.dp),
                            )
                            Spacer(Modifier.height(Spacing.lg))
                            Text(
                                "Camera access required",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                            )
                            Spacer(Modifier.height(Spacing.md))
                            Text(
                                "AI form analysis uses your camera to detect movement and count reps.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary,
                            )
                            Spacer(Modifier.height(Spacing.xl))
                            Button(
                                onClick = { cameraPermission.launchPermissionRequest() },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            ) {
                                Text("Enable Camera")
                            }
                        }
                    }
                }
            }

            // Stats row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                ) {
                    StatPill(
                        label = "Reps",
                        value = repCount.toString(),
                        color = colors.primary,
                        modifier = Modifier.weight(1f),
                    )
                    StatPill(
                        label = "Form Score",
                        value = "$formScore%",
                        color = when {
                            formScore >= 80 -> colors.success
                            formScore >= 60 -> colors.warning
                            else -> colors.error
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Form feedback
            if (feedback.isNotEmpty()) {
                item {
                    Text(
                        "Form Feedback",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                    )
                }
                items(feedback) { fb -> FormFeedbackCard(fb) }
            } else if (uiState.isAnalyzing) {
                item {
                    AxiomCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Analyzing your movement…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(Spacing.xl),
                        )
                    }
                }
            }

            // AI disclaimer
            item {
                Text(
                    "⚠️ AI form analysis is for guidance only. Consult a trainer for professional advice.",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
            }
        }
    }
}

@Composable
private fun CameraAnalysisPreview(
    onFrame: (androidx.camera.core.ImageProxy) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().also { analysis ->
                        analysis.setAnalyzer(executor) { imageProxy ->
                            onFrame(imageProxy)
                        }
                    }
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis,
                )
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = modifier,
    )
}

@Composable
private fun ExerciseChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AxiomTheme.colors
    val bgColor by animateColorAsState(
        if (selected) colors.primary else colors.card,
        animationSpec = tween(160),
        label = "chip_bg",
    )
    val textColor by animateColorAsState(
        if (selected) colors.textOnPrimary else colors.textSecondary,
        animationSpec = tween(160),
        label = "chip_text",
    )
    Box(
        modifier = modifier
            .clip(Radius.md)
            .background(bgColor)
            .border(1.dp, if (selected) colors.primary else colors.borderSubtle, Radius.md)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = textColor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
        }
    }
}

@Composable
private fun FormFeedbackCard(feedback: FormFeedback) {
    val colors = AxiomTheme.colors
    val (borderColor, emoji) = when (feedback.severity) {
        FeedbackSeverity.GOOD    -> Pair(colors.success, "✅")
        FeedbackSeverity.WARNING -> Pair(colors.warning, "⚠️")
        FeedbackSeverity.ERROR   -> Pair(colors.error, "❌")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radius.md)
            .background(colors.card)
            .border(
                width = 1.dp,
                color = borderColor.copy(alpha = 0.4f),
                shape = Radius.md,
            )
            .padding(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.width(Spacing.lg))
        Column {
            Text(
                feedback.bodyPart,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = borderColor,
            )
            Text(feedback.message, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
        }
    }
}

private fun ExerciseType.displayName() = when (this) {
    ExerciseType.SQUAT      -> "Squat"
    ExerciseType.PUSH_UP    -> "Push-up"
    ExerciseType.BICEP_CURL -> "Curl"
    ExerciseType.UNKNOWN    -> "Unknown"
}
