package com.axiom.aicoach.ui.screens.auth

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.components.AxiomPrimaryButton
import com.axiom.aicoach.ui.components.AxiomSecondaryButton
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing

private val letterSpacingTitle = 4.sp
private val letterSpacingSubtitle = 6.sp

data class WelcomeSlide(val emoji: String, val title: String, val subtitle: String)

private val slides = listOf(
    WelcomeSlide("🧠", "Your AI Personal Trainer", "Expert workout plans tailored to your body, goals, and schedule."),
    WelcomeSlide("🥗", "Smart Nutrition Coaching", "Log meals with your camera. Track macros effortlessly."),
    WelcomeSlide("📈", "See Real Progress", "Body scans, progress charts, and weekly AI coaching insights."),
)

@Composable
fun WelcomeScreen(
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onContinueWithGoogle: () -> Unit,
) {
    val colors = AxiomTheme.colors
    val pagerState = rememberPagerState { slides.size }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFFEFF6FF), colors.background))
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(Spacing.s48))

            // ── Logo ─────────────────────────────────────────────────────────
            Text(
                text = "AXIOM",
                style = MaterialTheme.typography.displayMedium,
                color = colors.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = letterSpacingTitle,
            )
            Text(
                text = "AI COACH",
                style = MaterialTheme.typography.labelLarge,
                color = colors.textMuted,
                letterSpacing = letterSpacingSubtitle,
            )

            Spacer(Modifier.height(Spacing.s64))

            // ── Carousel ─────────────────────────────────────────────────────
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                val slide = slides[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = slide.emoji,
                        style = MaterialTheme.typography.displayLarge,
                    )
                    Spacer(Modifier.height(Spacing.xxxl))
                    Text(
                        text = slide.title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(Spacing.xl))
                    Text(
                        text = slide.subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                }
            }

            // ── Page indicators ──────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = Spacing.xxxl),
            ) {
                repeat(slides.size) { i ->
                    val width by animateFloatAsState(
                        targetValue = if (i == pagerState.currentPage) 24f else 8f,
                        animationSpec = tween(180),
                        label = "dot_width_$i",
                    )
                    Box(
                        Modifier
                            .width(width.dp)
                            .height(8.dp)
                            .background(
                                color = if (i == pagerState.currentPage) colors.primary else colors.border,
                                shape = Radius.pill,
                            ),
                    )
                }
            }

            // ── CTA buttons ──────────────────────────────────────────────────
            AxiomPrimaryButton(
                text = "Get Started Free",
                onClick = onSignUp,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            AxiomSecondaryButton(
                text = "Sign In",
                onClick = onSignIn,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(20.dp))

            Text(
                text = "By continuing you agree to our Terms & Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = Spacing.xxxl),
            )
        }
    }
}

private val Int.sp get() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)
private val Float.sp get() = androidx.compose.ui.unit.TextUnit(this, androidx.compose.ui.unit.TextUnitType.Sp)
