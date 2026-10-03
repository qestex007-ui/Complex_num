package com.example

import com.example.model.ComplexNumber
import com.example.model.MathUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sqrt

class ExampleUnitTest {

    @Test
    fun testPythagoreanComplexNumber() {
        val c = ComplexNumber(3.0, 4.0)
        assertEquals(5.0, c.modulus, 1e-6)
        assertEquals(53.13, c.argumentDeg, 0.01)
        assertEquals("3 + 4i", c.toAlgebraicString())
        assertEquals("5 ∠ 53.13°", c.toPolarNotationString())
    }

    @Test
    fun testEulerIdentity() {
        val c = ComplexNumber.fromPolarDeg(1.0, 180.0)
        assertEquals(-1.0, c.real, 1e-6)
        assertEquals(0.0, c.imag, 1e-6)
        assertEquals("-1", c.toAlgebraicString())
    }

    @Test
    fun testPolarToAlgebraicConversion() {
        // r = 2, phi = 60 deg -> a = 1, b = sqrt(3) ~ 1.73205
        val c = ComplexNumber.fromPolarDeg(2.0, 60.0)
        assertEquals(1.0, c.real, 1e-5)
        assertEquals(sqrt(3.0), c.imag, 1e-5)
    }

    @Test
    fun testPiFractionDetection() {
        val piOver3 = MathUtils.findPiFraction(PI / 3.0)
        assertEquals("π/3", piOver3)

        val negPiOver4 = MathUtils.findPiFraction(-PI / 4.0)
        assertEquals("-π/4", negPiOver4)

        val twoPiOver3 = MathUtils.findPiFraction(2.0 * PI / 3.0)
        assertEquals("2π/3", twoPiOver3)
    }

    @Test
    fun testStepByStepDerivation() {
        val c = ComplexNumber(1.0, 1.0)
        val steps = c.getAlgebraicToPolarSteps()
        assertTrue(steps.isNotEmpty())
        assertTrue(steps[0].calculation.contains("√"))
        assertTrue(steps[1].title.contains("I квадрант"))
    }
}
