package com.axiom.aicoach.billing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscriptionTierTest {

    @Test
    fun `FREE_FEATURES dailyAiMessages is 5`() {
        assertEquals(5, FREE_FEATURES.dailyAiMessages)
    }

    @Test
    fun `PREMIUM_FEATURES dailyAiMessages is minus 1 (unlimited)`() {
        assertEquals(-1, PREMIUM_FEATURES.dailyAiMessages)
    }

    @Test
    fun `SubscriptionTier FREE features() returns FREE_FEATURES`() {
        assertEquals(FREE_FEATURES, SubscriptionTier.FREE.features())
    }

    @Test
    fun `FREE_FEATURES bodyAnalysisEnabled is false`() {
        assertFalse(FREE_FEATURES.bodyAnalysisEnabled)
    }

    @Test
    fun `PREMIUM_FEATURES bodyAnalysisEnabled is true`() {
        assertTrue(PREMIUM_FEATURES.bodyAnalysisEnabled)
    }
}
