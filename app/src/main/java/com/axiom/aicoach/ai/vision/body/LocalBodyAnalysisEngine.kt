package com.axiom.aicoach.ai.vision.body

import com.axiom.aicoach.data.local.entities.WeightLogEntity
import com.axiom.aicoach.data.local.entities.WorkoutSessionEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Heuristic body-analysis engine that derives scores entirely from
 * structured data (weight logs, workout sessions, photo count).
 *
 * No image or biometric inference is performed — the class is named
 * "Local" to distinguish it from a future cloud-backed variant.
 *
 * ## Scoring algorithm
 *
 * **transformationScore** (0–100):
 * ```
 * base 40
 * + weight-trend bonus  (0–30) — proportional to kg lost / days elapsed
 * + workout bonus       (0–20) — proportional to sessions in last 30 days
 * + streak/photo bonus  (0–10) — photo commitment signals consistency
 * ```
 *
 * **consistencyScore** (0–100):
 * ```
 * (completedSessions30Days / expectedSessions30Days) × 100, capped at 100
 * expectedSessions = 30 × (trainingDaysPerWeek / 7)  ← assumed 4 days/week
 * ```
 *
 * **estimatedProgressPercent**:
 * If a downward weight trend exists:
 * `(startWeight − currentWeight) / (startWeight − goalWeight) × 100`
 * Falls back to a session-based estimate when weight data is insufficient.
 */
@Singleton
class LocalBodyAnalysisEngine @Inject constructor() : BodyAnalysisEngine {

    // Assumed training frequency used when no profile data is available
    private val assumedTrainingDaysPerWeek = 4

    override suspend fun analyze(
        recentWeightLogs: List<WeightLogEntity>,
        recentSessions: List<WorkoutSessionEntity>,
        photoCount: Int,
        daysSinceStart: Int,
    ): BodyAnalysisResult {

        val today = LocalDate.now()
        val analysisDate = today.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))

        // ── Weight trend ──────────────────────────────────────────────────────
        // Logs are ordered DESC (most recent first) by the DAO query
        val currentWeight = recentWeightLogs.firstOrNull()?.weightKg
        val oldestWeight = recentWeightLogs.lastOrNull()?.weightKg
        val weightLost = if (currentWeight != null && oldestWeight != null) {
            (oldestWeight - currentWeight).coerceAtLeast(0f)
        } else 0f

        // Plateau detection: no change in last 14 days
        val cutoff14Days = LocalDateTime.now().minusDays(14).toString()
        val last14DayLogs = recentWeightLogs.filter { it.loggedAt >= cutoff14Days }
        val recentWeightRange = if (last14DayLogs.size >= 2) {
            val max = last14DayLogs.maxOf { it.weightKg }
            val min = last14DayLogs.minOf { it.weightKg }
            max - min
        } else null
        val isPlateau = recentWeightRange != null && recentWeightRange < 0.3f

        // ── Workout consistency ───────────────────────────────────────────────
        val cutoff30Days = LocalDateTime.now().minusDays(30).toString()
        val sessions30Days = recentSessions.count { session ->
            session.startedAt >= cutoff30Days && session.completedAt != null
        }
        val expectedSessions30Days = (30f * assumedTrainingDaysPerWeek / 7f).roundToInt()
            .coerceAtLeast(1)

        // ── Score computation ─────────────────────────────────────────────────

        // Weight-trend bonus: 1 kg lost per 30 days = +10 pts, up to +30 at 3+ kg
        val weightTrendBonus = if (daysSinceStart > 0) {
            val kgPerDay = weightLost / daysSinceStart.toFloat()
            val kgPer30Days = kgPerDay * 30f
            (kgPer30Days * 10f).roundToInt().coerceIn(0, 30)
        } else 0

        // Workout bonus: proportional to sessions completed vs expected
        val workoutBonus = ((sessions30Days.toFloat() / expectedSessions30Days) * 20f)
            .roundToInt().coerceIn(0, 20)

        // Photo/streak bonus: encourages documentation commitment
        val photoBonus = when {
            photoCount >= 10 -> 10
            photoCount >= 5  -> 7
            photoCount >= 2  -> 4
            photoCount >= 1  -> 2
            else             -> 0
        }

        val transformationScore = (40 + weightTrendBonus + workoutBonus + photoBonus)
            .coerceIn(0, 100)

        val consistencyScore = ((sessions30Days.toFloat() / expectedSessions30Days) * 100f)
            .roundToInt().coerceIn(0, 100)

        // ── Progress percent ──────────────────────────────────────────────────
        // We don't have goalWeight here; approximate using typical 0.5 kg/week target
        val estimatedProgressPercent = if (weightLost > 0f && daysSinceStart > 0) {
            val targetKgTotal = (daysSinceStart / 7f) * 0.5f   // 0.5 kg/week target
            ((weightLost / targetKgTotal.coerceAtLeast(0.1f)) * 100f).coerceIn(0f, 100f)
        } else {
            ((sessions30Days.toFloat() / expectedSessions30Days) * 100f).coerceIn(0f, 100f)
        }

        // ── Insights ──────────────────────────────────────────────────────────
        val insights = buildList {
            // Overall progress insight
            if (weightLost > 0.5f) {
                add(
                    BodyInsight(
                        category = InsightCategory.OVERALL_PROGRESS,
                        message = "You've lost ${String.format("%.1f", weightLost)} kg — great " +
                            "progress toward your transformation goal!",
                        sentiment = InsightSentiment.POSITIVE,
                    )
                )
            } else if (recentWeightLogs.isNotEmpty()) {
                add(
                    BodyInsight(
                        category = InsightCategory.OVERALL_PROGRESS,
                        message = "You're actively tracking your weight — consistency is the " +
                            "foundation of any transformation.",
                        sentiment = InsightSentiment.ENCOURAGING,
                    )
                )
            }

            // Consistency insight
            if (consistencyScore >= 80) {
                add(
                    BodyInsight(
                        category = InsightCategory.CONSISTENCY,
                        message = "Excellent workout consistency! You completed $sessions30Days " +
                            "sessions in the last 30 days.",
                        sentiment = InsightSentiment.POSITIVE,
                    )
                )
            } else if (consistencyScore >= 50) {
                add(
                    BodyInsight(
                        category = InsightCategory.CONSISTENCY,
                        message = "Good effort with $sessions30Days workouts this month. " +
                            "Aim for $expectedSessions30Days to hit your full potential.",
                        sentiment = InsightSentiment.ENCOURAGING,
                    )
                )
            } else {
                add(
                    BodyInsight(
                        category = InsightCategory.CONSISTENCY,
                        message = "Your best workout is the one you show up for. Even small " +
                            "increases in frequency compound over time.",
                        sentiment = InsightSentiment.ENCOURAGING,
                    )
                )
            }

            // Plateau recommendation
            if (isPlateau) {
                add(
                    BodyInsight(
                        category = InsightCategory.RECOMMENDATION,
                        message = "Your weight has been stable for 14+ days. Consider adjusting " +
                            "your calorie target or varying your training stimulus.",
                        sentiment = InsightSentiment.ENCOURAGING,
                    )
                )
            }

            // Posture note — always included (cannot detect from weight data)
            add(
                BodyInsight(
                    category = InsightCategory.POSTURE,
                    message = "Focus on maintaining a neutral spine during lifts. Good posture " +
                        "reduces injury risk and improves muscle activation.",
                    sentiment = InsightSentiment.NEUTRAL,
                )
            )

            // Symmetry note — always included
            add(
                BodyInsight(
                    category = InsightCategory.SYMMETRY,
                    message = "Include unilateral exercises (single-leg, single-arm) to address " +
                        "any left-right imbalances and build balanced strength.",
                    sentiment = InsightSentiment.NEUTRAL,
                )
            )
        }

        return BodyAnalysisResult(
            transformationScore = transformationScore,
            consistencyScore = consistencyScore,
            estimatedProgressPercent = estimatedProgressPercent,
            insights = insights,
            analysisDate = analysisDate,
        )
    }
}
