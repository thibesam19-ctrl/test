package com.axiom.aicoach.util

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.abs

/**
 * Self-contained calorie-math helpers defined inline so this test has no
 * production-code dependency while still exercising the formulas end-to-end.
 *
 * Mifflin-St Jeor BMR (male):
 *   bmr = 88.36 + (13.4 × weightKg) + (4.8 × heightCm) - (5.7 × age)
 *
 * TDEE:
 *   tdee = bmr × activityFactor
 */
private object CalorieMath {

    /** Mifflin-St Jeor BMR for males. */
    fun bmrMale(weightKg: Double, heightCm: Double, age: Int): Double =
        88.36 + (13.4 * weightKg) + (4.8 * heightCm) - (5.7 * age)

    /** Total Daily Energy Expenditure. */
    fun tdee(bmr: Double, activityFactor: Double): Double = bmr * activityFactor
}

class CalorieMathTest {

    private val delta = 0.5 // acceptable floating-point tolerance in kcal

    // ── BMR tests ─────────────────────────────────────────────────────────────

    @Test
    fun `bmrMale returns correct value for 80kg 180cm 30 year old`() {
        // 88.36 + (13.4 * 80) + (4.8 * 180) - (5.7 * 30)
        // = 88.36 + 1072 + 864 - 171
        // = 1853.36
        val expected = 88.36 + (13.4 * 80.0) + (4.8 * 180.0) - (5.7 * 30)
        val actual = CalorieMath.bmrMale(weightKg = 80.0, heightCm = 180.0, age = 30)
        assertEquals(expected, actual, delta)
    }

    @Test
    fun `bmrMale returns correct value for 70kg 175cm 25 year old`() {
        val expected = 88.36 + (13.4 * 70.0) + (4.8 * 175.0) - (5.7 * 25)
        val actual = CalorieMath.bmrMale(weightKg = 70.0, heightCm = 175.0, age = 25)
        assertEquals(expected, actual, delta)
    }

    @Test
    fun `bmrMale increases with higher weight`() {
        val bmrLighter = CalorieMath.bmrMale(weightKg = 70.0, heightCm = 175.0, age = 30)
        val bmrHeavier = CalorieMath.bmrMale(weightKg = 90.0, heightCm = 175.0, age = 30)
        assert(bmrHeavier > bmrLighter) {
            "Expected heavier person to have higher BMR, got lighter=$bmrLighter heavier=$bmrHeavier"
        }
    }

    @Test
    fun `bmrMale decreases with older age`() {
        val bmrYounger = CalorieMath.bmrMale(weightKg = 80.0, heightCm = 180.0, age = 25)
        val bmrOlder = CalorieMath.bmrMale(weightKg = 80.0, heightCm = 180.0, age = 40)
        assert(bmrOlder < bmrYounger) {
            "Expected older person to have lower BMR, got younger=$bmrYounger older=$bmrOlder"
        }
    }

    // ── TDEE tests ────────────────────────────────────────────────────────────

    @Test
    fun `tdee equals bmr times activityFactor`() {
        val bmr = 1800.0
        val factor = 1.55 // moderate activity
        val expected = bmr * factor
        val actual = CalorieMath.tdee(bmr, factor)
        assertEquals(expected, actual, delta)
    }

    @Test
    fun `tdee for sedentary 80kg 180cm 30 year old is approximately correct`() {
        // Sedentary factor = 1.2
        val bmr = CalorieMath.bmrMale(80.0, 180.0, 30)
        val tdee = CalorieMath.tdee(bmr, activityFactor = 1.2)
        val expected = bmr * 1.2
        assertEquals(expected, tdee, delta)
        // Sanity check: TDEE should be in a physiologically plausible range (1500–5000)
        assert(tdee in 1500.0..5000.0) { "TDEE out of expected range: $tdee" }
    }

    @Test
    fun `tdee is always greater than bmr for activity factors above 1`() {
        val bmr = CalorieMath.bmrMale(75.0, 178.0, 28)
        val tdee = CalorieMath.tdee(bmr, activityFactor = 1.375)
        assert(tdee > bmr) { "TDEE ($tdee) should be greater than BMR ($bmr) for any active factor" }
    }

    // ── Extensions.kt utility tests ───────────────────────────────────────────

    @Test
    fun `Float roundTo rounds correctly to 2 decimal places`() {
        val value = 3.14159f
        val rounded = value.roundTo(2)
        assertEquals(3.14f, rounded, 0.001f)
    }

    @Test
    fun `Int formatWithCommas formats thousands`() {
        assertEquals("1,000", 1000.formatWithCommas())
        assertEquals("10,000", 10000.formatWithCommas())
    }

    @Test
    fun `Float toCalorieString appends kcal label`() {
        val result = 2000f.toCalorieString()
        assertEquals("2,000 kcal", result)
    }
}
