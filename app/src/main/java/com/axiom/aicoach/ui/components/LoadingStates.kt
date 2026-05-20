package com.axiom.aicoach.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Spacing

// ---------------------------------------------------------------------------
// AxiomLoadingOverlay
// ---------------------------------------------------------------------------

/**
 * Wraps [content] and overlays a semi-transparent scrim with a centered
 * [CircularProgressIndicator] when [isLoading] is true. The content remains
 * visible but non-interactive beneath the overlay.
 */
@Composable
fun AxiomLoadingOverlay(
    isLoading: Boolean,
    content: @Composable () -> Unit,
) {
    val colors = AxiomTheme.colors
    Box(modifier = Modifier.fillMaxSize()) {
        content()
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = colors.primary,
                    strokeWidth = 3.dp,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// AxiomFullScreenLoading
// ---------------------------------------------------------------------------

/**
 * Full-screen centered loading state with a shimmer-style animated background
 * gradient and an optional [message] label beneath the spinner.
 */
@Composable
fun AxiomFullScreenLoading(message: String = "Loading...") {
    val colors = AxiomTheme.colors

    val infiniteTransition = rememberInfiniteTransition(label = "full_screen_shimmer")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "full_screen_shimmer_progress",
    )

    val shimmerColors = listOf(
        colors.background,
        colors.borderSubtle.copy(alpha = 0.4f),
        colors.background,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(x = (shimmerProgress - 0.3f) * 1400f, y = 0f),
                    end = Offset(x = (shimmerProgress + 0.3f) * 1400f, y = 0f),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(
                color = colors.primary,
                strokeWidth = 3.dp,
            )
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.xl))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// AxiomRetryState
// ---------------------------------------------------------------------------

/**
 * Error state composable that surfaces a [message] and a ghost-style retry
 * button. Use in place of content when a network or data load fails.
 */
@Composable
fun AxiomRetryState(
    message: String,
    onRetry: () -> Unit,
) {
    val colors = AxiomTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.s40),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(Spacing.xl))
        AxiomGhostButton(
            text = "Retry",
            onClick = onRetry,
        )
    }
}

// ---------------------------------------------------------------------------
// AiTypingIndicator
// ---------------------------------------------------------------------------

/**
 * Animated 3-dot typing indicator, suitable for AI-response pending states.
 *
 * Each dot pulses its alpha on a staggered cycle (0 ms, 150 ms, 300 ms
 * offset) using an [infiniteRepeatable] tween so the animation loops
 * continuously without interruption.
 */
@Composable
fun AiTypingIndicator() {
    val colors = AxiomTheme.colors
    val color = colors.textSecondary

    val infiniteTransition = rememberInfiniteTransition(label = "typing_indicator")

    @Composable
    fun dotAlpha(delayMs: Int): Float {
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 600,
                    delayMillis = delayMs,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "dot_alpha_$delayMs",
        )
        return alpha
    }

    val alpha0 = dotAlpha(0)
    val alpha1 = dotAlpha(150)
    val alpha2 = dotAlpha(300)

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = alpha0)),
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = alpha1)),
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = alpha2)),
        )
    }
}
