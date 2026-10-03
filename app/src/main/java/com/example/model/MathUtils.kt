package com.example.model

import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt

object MathUtils {

    fun formatDouble(value: Double, precision: Int = 4): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "∞" else "-∞"

        // Check if practically an integer
        val rounded = (value * Math.pow(10.0, precision.toDouble())).roundToInt() / Math.pow(10.0, precision.toDouble())
        if (abs(rounded - rounded.toLong()) < 1e-9) {
            return rounded.toLong().toString()
        }

        val pattern = "%.${precision}f"
        val formatted = String.format(Locale.US, pattern, rounded)
            .trimEnd('0')
            .trimEnd('.')
        return if (formatted == "-0") "0" else formatted
    }

    /**
     * Looks for exact pi fractions like 0, pi/6, pi/4, pi/3, pi/2, 2pi/3, 3pi/4, 5pi/6, pi, etc.
     */
    fun findPiFraction(radians: Double, tolerance: Double = 0.008): String? {
        val normalized = normalizeAngleRad(radians)
        val factor = normalized / PI // ratio to pi

        val denominators = listOf(1, 2, 3, 4, 6, 8, 12)
        for (d in denominators) {
            val n = (factor * d).roundToInt()
            if (abs(factor - n.toDouble() / d) < tolerance) {
                return when {
                    n == 0 -> "0"
                    n == d -> "π"
                    n == -d -> "-π"
                    n == 1 -> "π/$d"
                    n == -1 -> "-π/$d"
                    d == 1 -> "${n}π"
                    else -> "${n}π/$d"
                }
            }
        }
        return null
    }

    /**
     * Normalizes angle into (-PI, PI]
     */
    fun normalizeAngleRad(radians: Double): Double {
        var a = radians % (2 * PI)
        if (a <= -PI) a += 2 * PI
        if (a > PI) a -= 2 * PI
        return a
    }

    /**
     * Normalizes angle into (-180, 180]
     */
    fun normalizeAngleDeg(degrees: Double): Double {
        var d = degrees % 360.0
        if (d <= -180.0) d += 360.0
        if (d > 180.0) d -= 360.0
        return d
    }

    /**
     * Positive degrees in range [0, 360)
     */
    fun toPositiveDeg(degrees: Double): Double {
        var d = degrees % 360.0
        if (d < 0) d += 360.0
        return d
    }
}
