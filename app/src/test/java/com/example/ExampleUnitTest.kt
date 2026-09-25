package com.example

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.sqrt

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testMagneticMagnitudeCalculation() {
        val x = 30f
        val y = 40f
        val z = 0f
        val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        assertEquals(50f, magnitude, 0.001f)
    }

    @Test
    fun testMagneticIntensityThresholdClassification() {
        fun classifyIntensity(magnitude: Float): String {
            return when {
                magnitude < 55f -> "NORMAL"
                magnitude < 85f -> "ELEVATED"
                else -> "STRONG"
            }
        }

        assertEquals("NORMAL", classifyIntensity(42f))
        assertEquals("NORMAL", classifyIntensity(54.9f))
        assertEquals("ELEVATED", classifyIntensity(55f))
        assertEquals("ELEVATED", classifyIntensity(75f))
        assertEquals("STRONG", classifyIntensity(85f))
        assertEquals("STRONG", classifyIntensity(110f))
    }

    @Test
    fun testRtlLanguageDetection() {
        fun isRtl(lang: String): Boolean = lang == "ur" || lang == "ar"

        assertTrue(isRtl("ur"))
        assertTrue(isRtl("ar"))
        assertFalse(isRtl("en"))
        assertFalse(isRtl("hi"))
    }
}
