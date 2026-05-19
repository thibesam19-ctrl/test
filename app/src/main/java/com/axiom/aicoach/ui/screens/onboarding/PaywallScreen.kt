package com.axiom.aicoach.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.aicoach.ui.components.AxiomPrimaryButton
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
                .padding(horizontal = Spacing.xl)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(Spacing.s48))

            Text("✨", style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(Spacing.xl))
            Text(
                "Unlock Your Full Potential",
                style = MaterialTheme.typography.headlineLarge,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                "Try Axiom Premium free for 7 days",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(Spacing.s32))

            // Plan cards
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

            // Features
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                premiumFeatures.forEach { feature ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, null, tint = colors.success, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(feature, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                    }
                }
            }

            Spacer(Modifier.height(Spacing.s40))
            AxiomPrimaryButton("Start Free Trial", onContinue, Modifier.fillMaxWidth())
            Spacer(Modifier.height(Spacing.xl))
            TextButton(onClick = onContinue) {
                Text("Continue with free plan", color = colors.textMuted)
            }
            Spacer(Modifier.height(Spacing.md))
            Text(
                "Cancel anytime. No commitment. Restore purchases button in Settings.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Spacing.xxxl))
        }
    }
}

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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radius.lg)
            .background(if (selected) colors.primaryLight else colors.card)
            .border(2.dp, if (selected) colors.primary else colors.borderSubtle, Radius.lg)
            .clickable(onClick = onClick)
            .padding(Spacing.xl),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(label, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                    if (badge != null) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            Modifier
                                .clip(Radius.pill)
                                .background(colors.accent)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(badge, style = MaterialTheme.typography.labelSmall, color = colors.textOnPrimary)
                        }
                    }
                }
                if (equivalent != null) {
                    Text(equivalent, style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                }
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(price, style = MaterialTheme.typography.headlineMedium, color = if (selected) colors.primary else colors.textPrimary, fontWeight = FontWeight.Bold)
                Text(perPeriod, style = MaterialTheme.typography.bodySmall, color = colors.textMuted, modifier = Modifier.padding(bottom = 4.dp))
            }
        }
        RadioButton(
            selected = selected,
            onClick = onClick,
            modifier = Modifier.align(Alignment.TopEnd).size(0.dp), // hidden, visual handled by border
            colors = RadioButtonDefaults.colors(selectedColor = colors.primary),
        )
    }
}
