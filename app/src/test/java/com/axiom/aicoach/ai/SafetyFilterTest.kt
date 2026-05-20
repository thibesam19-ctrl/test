package com.axiom.aicoach.ai

import com.axiom.aicoach.ai.coaching.SafetyFilter
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class SafetyFilterTest {

    private lateinit var safetyFilter: SafetyFilter

    @Before
    fun setUp() {
        safetyFilter = SafetyFilter()
    }

    // ── isEatingDisorderLanguage (via check()) returns triggered=true ─────────

    @Test
    fun `check returns unsafe for 'not eaten all day'`() {
        val result = safetyFilter.check("I have not eaten all day and feel fine")
        assertFalse("Expected isSafe=false for ED trigger", result.isSafe)
    }

    @Test
    fun `check returns unsafe for 'starving myself'`() {
        val result = safetyFilter.check("I've been starving myself to lose weight faster")
        assertFalse("Expected isSafe=false for ED trigger", result.isSafe)
    }

    @Test
    fun `check returns unsafe for 'hate my body'`() {
        val result = safetyFilter.check("I just hate my body so much right now")
        assertFalse("Expected isSafe=false for ED trigger", result.isSafe)
    }

    @Test
    fun `check returns unsafe for '1200 calories is too much'`() {
        // "starving" is in the trigger list and aligns with extreme restriction language
        val result = safetyFilter.check("1200 calories is too much, I'm basically starving")
        assertFalse("Expected isSafe=false for ED trigger", result.isSafe)
    }

    @Test
    fun `check returns unsafe for 'purging'`() {
        val result = safetyFilter.check("I've been purging after meals")
        assertFalse("Expected isSafe=false for ED trigger", result.isSafe)
    }

    // ── isEatingDisorderLanguage returns false for normal content ─────────────

    @Test
    fun `check returns safe for 'I ate a salad today'`() {
        val result = safetyFilter.check("I ate a salad today and felt great")
        assertTrue("Expected isSafe=true for normal content", result.isSafe)
    }

    @Test
    fun `check returns safe for 'trying to lose weight healthily'`() {
        val result = safetyFilter.check("I'm trying to lose weight healthily through exercise")
        assertTrue("Expected isSafe=true for normal content", result.isSafe)
    }

    @Test
    fun `check returns safe for 'my workout was great'`() {
        val result = safetyFilter.check("my workout was great today, hit a new PR")
        assertTrue("Expected isSafe=true for normal content", result.isSafe)
    }

    // ── filterMessage (check()) returns non-null guardrail response for ED ────

    @Test
    fun `check returns non-null safeResponse for ED content`() {
        val result = safetyFilter.check("I've been starving myself")
        assertNotNull("Expected a guardrail safeResponse for ED content", result.safeResponse)
    }

    @Test
    fun `check returns non-null safeResponse for hate my body`() {
        val result = safetyFilter.check("I hate my body")
        assertNotNull("Expected a guardrail safeResponse for ED content", result.safeResponse)
    }

    // ── filterMessage returns null safeResponse for normal fitness content ────

    @Test
    fun `check returns null safeResponse for normal fitness message`() {
        val result = safetyFilter.check("Can you help me plan my chest workout?")
        assertNull("Expected null safeResponse for safe content", result.safeResponse)
    }

    @Test
    fun `check returns null safeResponse for calorie tracking message`() {
        val result = safetyFilter.check("I had 2000 calories today, is that on track?")
        assertNull("Expected null safeResponse for safe content", result.safeResponse)
    }
}
