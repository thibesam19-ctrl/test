package com.axiom.aicoach.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.components.AxiomCard
import com.axiom.aicoach.ui.components.AxiomGhostButton
import com.axiom.aicoach.ui.components.AxiomPrimaryButton
import com.axiom.aicoach.ui.components.PillBadge
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing

@Composable
fun PaywallScreen(onContinue: () -> Unit) {
    val colors = AxiomTheme.colors
    var selectedPlan by remember { mutableStateOf("annual") }

    val premiumFeatures = listOf(
        "Unlimited AI-generated workout plans",
        "Food scanning (camera + barcode)",
        "Body progress photos & tracking",
        "Personalized meal planning",
        "Wearable device integrations",
        "Unlimited AI coach messages",
        "Advanced analytics & data export",
        "All achievements & weekly challenges",
    )

    Scaffold(containerColor = colors.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ── Hero gradient box ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(colors.primaryDark, colors.primary),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "✨ Axiom Premium",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(Spacing.s32))

            Column(modifier = Modifier.padding(horizontal = Spacing.xl)) {
                // ── Plan cards ────────────────────────────────────────────────
                PlanCard(
                    label = "Annual",
                    badge = "Most Popular · 50% off",
                    price = "$59.99",
                    perPeriod = "/year",
                    equivalent = "Only $5/month",
                    selected = selectedPlan == "annual",
                    onClick = { selectedPlan = "annual" },
                )
                Spacer(Modifier.height(Spacing.lg))
                PlanCard(
                    label = "Monthly",
                    price = "$9.99",
                    perPeriod = "/month",
                    selected = selectedPlan == "monthly",
                    onClick = { selectedPlan = "monthly" },
                )
                Spacer(Modifier.height(Spacing.lg))
                PlanCard(
                    label = "Lifetime",
                    badge = "Limited Time",
                    price = "$199.99",
                    perPeriod = " once",
                    equivalent = "Pay once, keep forever",
                    selected = selectedPlan == "lifetime",
                    onClick = { selectedPlan = "lifetime" },
                )

                Spacer(Modifier.height(Spacing.s32))

                // ── Features list ─────────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    premiumFeatures.forEach { feature ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.success,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = feature,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textPrimary,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(Spacing.s40))

                // ── Footer CTA ────────────────────────────────────────────────
                AxiomPrimaryButton(
                    text = "Start 7-Day Free Trial",
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(Spacing.lg))
                AxiomGhostButton(
                    text = "Continue with free plan",
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(Spacing.md))
                Text(
                    text = "Cancel anytime. No commitment.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(Spacing.xl))
                TextButton(
                    onClick = { /* restore purchases */ },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text(
                        text = "Restore Purchases",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textMuted,
                    )
                }

                Spacer(Modifier.height(Spacing.xxxl))
            }
        }
    }
}

// ── PlanCard ──────────────────────────────────────────────────────────────────

@Composable
private fun PlanCard(
    label: String,
    price: String,
    perPeriod: String,
    selected: Boolean,
    onClick: () -> Unit,
    badge: String? = null,
    equivalent: String? = null,
) {
    val colors = AxiomTheme.colors
    AxiomCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) colors.primary else colors.borderSubtle,
                shape = Radius.lg,
            )
            .background(
                color = if (selected) colors.primaryLight else colors.card,
                shape = Radius.lg,
            )
            .clip(Radius.lg)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (selected) colors.primaryLight else colors.card)
                .padding(Spacing.xl),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selected) colors.primary else colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                        if (badge != null) {
                            Spacer(Modifier.width(8.dp))
                            PillBadge(text = badge, color = colors.accent)
                        }
                    }
                    if (equivalent != null) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = equivalent,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
                        )
                    }
                }
                Spacer(Modifier.width(Spacing.xl))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = price,
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (selected) colors.primary else colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = perPeriod,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
            }
        }
    }
}
