package com.axiom.aicoach.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.aicoach.ai.consent.AiConsentManager
import com.axiom.aicoach.ui.components.AxiomCard
import com.axiom.aicoach.ui.components.AxiomGhostButton
import com.axiom.aicoach.ui.components.AxiomPrimaryButton
import com.axiom.aicoach.ui.components.AxiomTopBar
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Spacing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── ViewModel ─────────────────────────────────────────────────────────────────

data class AiConsentUiState(
    val hasAiConsent: Boolean = false,
    val hasCameraAiConsent: Boolean = false,
)

@HiltViewModel
class AiConsentViewModel @Inject constructor(
    private val consentManager: AiConsentManager,
) : ViewModel() {

    val uiState: StateFlow<AiConsentUiState> = combine(
        consentManager.hasConsentedToAiFeatures,
        consentManager.hasConsentedToCameraAi,
    ) { ai, camera -> AiConsentUiState(ai, camera) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AiConsentUiState())

    fun grantAll() = viewModelScope.launch {
        consentManager.grantAiConsent()
        consentManager.grantCameraAiConsent()
    }

    fun grantAiOnly() = viewModelScope.launch {
        consentManager.grantAiConsent()
    }

    fun revokeAll() = viewModelScope.launch {
        consentManager.revokeAllAiConsent()
    }
}

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun AiConsentScreen(
    onBack: () -> Unit,
    viewModel: AiConsentViewModel = hiltViewModel(),
) {
    val colors = AxiomTheme.colors
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { AxiomTopBar("AI Features & Privacy", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        ) {
            Spacer(Modifier.height(Spacing.md))

            // Hero
            Text(
                "AI-Powered Intelligence",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            Text(
                "Axiom uses on-device AI to personalize your fitness experience. Your data stays private — AI processing happens locally first.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )

            // Feature list
            listOf(
                Triple("🤖", "AI Coach", "Chat with a context-aware coach that knows your calories, workouts, and progress."),
                Triple("📊", "Smart Insights", "Detect plateaus, low protein, missed workouts, and get adaptive recommendations."),
                Triple("📷", "Food Recognition", "Point your camera at food for automatic calorie estimation. Processing is on-device."),
                Triple("🏋️", "Form Analysis", "Real-time pose detection to count reps and give form feedback. Camera data never leaves your device."),
                Triple("📈", "Body Analysis", "Visual transformation scoring based on your weight logs and workout history."),
            ).forEach { (emoji, title, desc) ->
                AxiomCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(Spacing.xl),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(emoji, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.width(Spacing.lg))
                        Column {
                            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                            Spacer(Modifier.height(4.dp))
                            Text(desc, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                    }
                }
            }

            // Privacy commitments
            AxiomCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(Spacing.xl)) {
                    Text(
                        "🔒 Privacy Commitments",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                    )
                    Spacer(Modifier.height(Spacing.lg))
                    listOf(
                        "Camera frames are processed on-device and never stored",
                        "AI coach conversations are saved locally in your encrypted database",
                        "No health or biometric data is sold to third parties",
                        "You can delete all AI data at any time from Settings",
                        "Cloud AI (OpenAI/Claude/Gemini) is opt-in and requires your API key",
                    ).forEach { point ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Text("•", color = colors.primary, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.width(Spacing.md))
                            Text(point, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                    }
                }
            }

            // Medical disclaimer
            AxiomCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(Spacing.xl)) {
                    Text(
                        "⚠️ Important Disclaimer",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.warning,
                    )
                    Spacer(Modifier.height(Spacing.md))
                    Text(
                        "Axiom AI provides fitness guidance for informational purposes only. It is not a substitute for professional medical advice, diagnosis, or treatment. Body analysis results are visual estimations only. Always consult a qualified health professional before making significant changes to your diet or exercise routine.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                    )
                }
            }

            // Current status
            if (uiState.hasAiConsent) {
                AxiomCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.xl)) {
                        Text("✅ AI Features Enabled", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = colors.success)
                        Spacer(Modifier.height(Spacing.sm))
                        Text(
                            "Camera AI: ${if (uiState.hasCameraAiConsent) "Enabled" else "Disabled"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                        )
                        Spacer(Modifier.height(Spacing.lg))
                        AxiomGhostButton(
                            text = "Revoke AI Consent",
                            onClick = { viewModel.revokeAll() },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            } else {
                AxiomPrimaryButton(
                    text = "Enable All AI Features",
                    onClick = { viewModel.grantAll() },
                    modifier = Modifier.fillMaxWidth(),
                )
                AxiomGhostButton(
                    text = "Enable AI Coach Only (no camera)",
                    onClick = { viewModel.grantAiOnly() },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(Spacing.xl))
        }
    }
}
