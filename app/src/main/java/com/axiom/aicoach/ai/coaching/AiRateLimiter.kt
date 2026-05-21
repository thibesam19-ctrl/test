package com.axiom.aicoach.ai.coaching

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Per-session AI request rate limiter.
 *
 * Enforces:
 *  - MAX_REQUESTS_PER_MINUTE: burst protection
 *  - MAX_REQUESTS_PER_DAY: daily token/cost budget
 *
 * Not a distributed rate limiter — scope is one app install / one session.
 * For server-side enforcement, pair this with backend quota checks.
 */
@Singleton
class AiRateLimiter @Inject constructor() {

    private val minuteWindowStart = AtomicLong(System.currentTimeMillis())
    private val minuteCount = AtomicInteger(0)

    private val dayWindowStart = AtomicLong(System.currentTimeMillis())
    private val dayCount = AtomicInteger(0)

    data class RateCheckResult(val allowed: Boolean, val reason: String? = null)

    @Synchronized
    fun checkAndRecord(): RateCheckResult {
        val now = System.currentTimeMillis()

        // Reset minute window
        if (now - minuteWindowStart.get() >= MINUTE_MS) {
            minuteWindowStart.set(now)
            minuteCount.set(0)
        }

        // Reset day window
        if (now - dayWindowStart.get() >= DAY_MS) {
            dayWindowStart.set(now)
            dayCount.set(0)
        }

        if (minuteCount.get() >= MAX_REQUESTS_PER_MINUTE) {
            return RateCheckResult(
                allowed = false,
                reason = "You're sending messages too quickly. Please wait a moment before trying again.",
            )
        }

        if (dayCount.get() >= MAX_REQUESTS_PER_DAY) {
            return RateCheckResult(
                allowed = false,
                reason = "You've reached your daily AI coaching limit. Your limit resets at midnight.",
            )
        }

        minuteCount.incrementAndGet()
        dayCount.incrementAndGet()
        return RateCheckResult(allowed = true)
    }

    fun getRemainingToday(): Int = maxOf(0, MAX_REQUESTS_PER_DAY - dayCount.get())

    companion object {
        private const val MINUTE_MS = 60_000L
        private const val DAY_MS = 86_400_000L
        const val MAX_REQUESTS_PER_MINUTE = 10
        const val MAX_REQUESTS_PER_DAY = 200
    }
}
