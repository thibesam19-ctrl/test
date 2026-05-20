package com.axiom.aicoach.ui.screens.legal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.components.AxiomCard
import com.axiom.aicoach.ui.components.AxiomTopBar
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Spacing

@Composable
fun LegalHubScreen(
    onPrivacyPolicy: () -> Unit,
    onTermsOfService: () -> Unit,
    onBack: () -> Unit,
) {
    val colors = AxiomTheme.colors

    Scaffold(
        topBar = { AxiomTopBar("Legal", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
        ) {
            Spacer(Modifier.height(Spacing.xl))

            Text(
                text = "Legal Documents",
                style = MaterialTheme.typography.labelLarge,
                color = colors.textSecondary,
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.md),
            )

            Spacer(Modifier.height(Spacing.md))

            AxiomCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    LegalHubRow(
                        icon = Icons.Default.Policy,
                        title = "Privacy Policy",
                        subtitle = "How we collect and use your data",
                        onClick = onPrivacyPolicy,
                    )
                    HorizontalDivider(
                        color = colors.borderSubtle,
                        modifier = Modifier.padding(start = 60.dp),
                    )
                    LegalHubRow(
                        icon = Icons.Default.Gavel,
                        title = "Terms of Service",
                        subtitle = "Terms and conditions of use",
                        onClick = onTermsOfService,
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = "Axiom AI Coach\nVersion 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.s80),
            )
        }
    }
}

@Composable
private fun LegalHubRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.xl, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(Spacing.xl))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(18.dp),
        )
    }
}
