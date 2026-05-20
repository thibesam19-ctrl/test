package com.axiom.aicoach.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing

// ---------------------------------------------------------------------------
// PremiumFeatureBadge
// ---------------------------------------------------------------------------

/**
 * Small pill-shaped "PRO" badge with a crown emoji, rendered in the app's
 * primary colour. Drop this next to feature titles or menu items that require
 * a paid subscription.
 */
@Composable
fun PremiumFeatureBadge(modifier: Modifier = Modifier) {
    val colors = AxiomTheme.colors
    Box(
        modifier = modifier
            .background(color = colors.primary, shape = Radius.pill)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "👑 PRO",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textOnPrimary,
            fontWeight = FontWeight.Bold,
        )
    }
}

// ---------------------------------------------------------------------------
// FeatureLockedCard
// ---------------------------------------------------------------------------

/**
 * A card shown in place of a gated feature.
 *
 * Displays a lock icon, the [featureName], a short prompt, and an
 * "Upgrade to Premium" CTA button. Tapping the button invokes [onUpgrade].
 */
@Composable
fun FeatureLockedCard(
    featureName: String,
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked feature",
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = featureName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                )
            }
            Text(
                text = "Unlock this feature by upgrading to Premium.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(Spacing.sm))
            AxiomPrimaryButton(
                text = "Upgrade to Premium",
                onClick = onUpgrade,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// GatedContent
// ---------------------------------------------------------------------------

/**
 * Conditionally shows [content] or [lockedContent] based on [isUnlocked].
 *
 * Use this wrapper around any UI section that requires a paid subscription:
 *
 * ```kotlin
 * GatedContent(
 *     isUnlocked = subscriptionManager.isFeatureUnlocked("body_analysis"),
 *     lockedContent = {
 *         FeatureLockedCard(featureName = "Body Analysis", onUpgrade = { navController.navigate("paywall") })
 *     },
 * ) {
 *     BodyAnalysisContent()
 * }
 * ```
 */
@Composable
fun GatedContent(
    isUnlocked: Boolean,
    lockedContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    if (isUnlocked) {
        content()
    } else {
        lockedContent()
    }
}
