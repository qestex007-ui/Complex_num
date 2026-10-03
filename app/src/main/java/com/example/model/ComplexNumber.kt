package com.example.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt

data class MathStep(
    val title: String,
    val formula: String,
    val calculation: String,
    val note: String? = null
)

data class ComplexNumber(
    val real: Double,
    val imag: Double
) {
    val modulus: Double = hypot(real, imag)
    val argumentRad: Double = atan2(imag, real)
    val argumentDeg: Double = Math.toDegrees(argumentRad)

    companion object {
        val ZERO = ComplexNumber(0.0, 0.0)

        fun fromAlgebraic(re: Double, im: Double): ComplexNumber {
            return ComplexNumber(re, im)
        }

        fun fromPolarRad(r: Double, phiRad: Double): ComplexNumber {
            val validR = if (r < 0) 0.0 else r
            val re = validR * cos(phiRad)
            val im = validR * sin(phiRad)
            return ComplexNumber(re, im)
        }

        fun fromPolarDeg(r: Double, phiDeg: Double): ComplexNumber {
            return fromPolarRad(r, Math.toRadians(phiDeg))
        }
    }

    fun toAlgebraicString(useJ: Boolean = false, precision: Int = 4): String {
        val unit = if (useJ) "j" else "i"
        val reStr = MathUtils.formatDouble(real, precision)
        val imStr = MathUtils.formatDouble(imag, precision)

        if (modulus < 1e-9) return "0"

        if (kotlin.math.abs(real) < 1e-9) {
            return when {
                imStr == "1" -> unit
                imStr == "-1" -> "-$unit"
                else -> if (useJ) "$unit$imStr" else "$imStr$unit"
            }
        }

        if (kotlin.math.abs(imag) < 1e-9) {
            return reStr
        }

        val sign = if (imag > 0) " + " else " - "
        val absImStr = MathUtils.formatDouble(kotlin.math.abs(imag), precision)
        val imPart = when {
            absImStr == "1" -> unit
            useJ -> "$unit$absImStr"
            else -> "$absImStr$unit"
        }

        return "$reStr$sign$imPart"
    }

    fun toExponentialString(
        useJ: Boolean = false,
        useDegrees: Boolean = false,
        precision: Int = 4
    ): String {
        val unit = if (useJ) "j" else "i"
        if (modulus < 1e-9) return "0"

        val rStr = MathUtils.formatDouble(modulus, precision)
        val rPrefix = if (rStr == "1") "" else "$rStr · "

        return if (useDegrees) {
            val degStr = MathUtils.formatDouble(argumentDeg, 2)
            "$rPrefix e^($unit · $degStr°)"
        } else {
            val piFraction = MathUtils.findPiFraction(argumentRad)
            val angleStr = piFraction ?: "${MathUtils.formatDouble(argumentRad, precision)} rad"
            "$rPrefix e^($unit · $angleStr)"
        }
    }

    fun toTrigonometricString(
        useJ: Boolean = false,
        useDegrees: Boolean = false,
        precision: Int = 4
    ): String {
        val unit = if (useJ) "j" else "i"
        if (modulus < 1e-9) return "0"

        val rStr = MathUtils.formatDouble(modulus, precision)
        val angleStr = if (useDegrees) {
            "${MathUtils.formatDouble(argumentDeg, 2)}°"
        } else {
            MathUtils.findPiFraction(argumentRad) ?: "${MathUtils.formatDouble(argumentRad, precision)}"
        }

        val sign = if (imag < 0 && useDegrees) "-" else "+"
        val prefix = if (rStr == "1") "" else "$rStr · "

        return "$prefix(cos($angleStr) + $unit · sin($angleStr))"
    }

    fun toPolarNotationString(precision: Int = 4): String {
        if (modulus < 1e-9) return "0 ∠ 0°"
        val rStr = MathUtils.formatDouble(modulus, precision)
        val degStr = MathUtils.formatDouble(argumentDeg, 2)
        return "$rStr ∠ $degStr°"
    }

    fun getAlgebraicToPolarSteps(useJ: Boolean = false, precision: Int = 4): List<MathStep> {
        val steps = mutableListOf<MathStep>()
        val aStr = MathUtils.formatDouble(real, precision)
        val bStr = MathUtils.formatDouble(imag, precision)

        // Step 1: Modulus
        val a2 = real * real
        val b2 = imag * imag
        val sumSq = a2 + b2
        val rVal = modulus
        val rStr = MathUtils.formatDouble(rVal, precision)

        steps.add(
            MathStep(
                title = "1. Вычисление модуля (r или |z|)",
                formula = "r = |z| = √(a² + b²)",
                calculation = "r = √(($aStr)² + ($bStr)²) = √(${MathUtils.formatDouble(a2, 2)} + ${MathUtils.formatDouble(b2, 2)}) = √(${MathUtils.formatDouble(sumSq, 2)}) = $rStr",
                note = "Модуль представляет собой расстояние от начала координат (0, 0) до точки ($aStr, $bStr) на комплексной плоскости."
            )
        )

        // Step 2: Argument
        if (rVal < 1e-9) {
            steps.add(
                MathStep(
                    title = "2. Вычисление аргумента (φ)",
                    formula = "φ = Arg(z)",
                    calculation = "Точка находится в начале координат (0, 0). Аргумент не определён.",
                    note = "Для z = 0 модуль равен 0, а аргумент может принимать любое значение."
                )
            )
        } else {
            val quadrantName: String
            val phiFormula: String
            val phiCalc: String

            val piFraction = MathUtils.findPiFraction(argumentRad)
            val radStr = MathUtils.formatDouble(argumentRad, precision)
            val degStr = MathUtils.formatDouble(argumentDeg, 2)

            when {
                kotlin.math.abs(imag) < 1e-9 && real > 0 -> {
                    quadrantName = "Положительная вещественная полуось (b = 0, a > 0)"
                    phiFormula = "φ = 0"
                    phiCalc = "φ = 0 рад = 0°"
                }
                kotlin.math.abs(imag) < 1e-9 && real < 0 -> {
                    quadrantName = "Отрицательная вещественная полуось (b = 0, a < 0)"
                    phiFormula = "φ = π"
                    phiCalc = "φ = π рад = 180°"
                }
                kotlin.math.abs(real) < 1e-9 && imag > 0 -> {
                    quadrantName = "Положительная мнимая полуось (a = 0, b > 0)"
                    phiFormula = "φ = π / 2"
                    phiCalc = "φ = π/2 рад = 90°"
                }
                kotlin.math.abs(real) < 1e-9 && imag < 0 -> {
                    quadrantName = "Отрицательная мнимая полуось (a = 0, b < 0)"
                    phiFormula = "φ = -π / 2"
                    phiCalc = "φ = -π/2 рад = -90° (или 270°)"
                }
                real > 0 && imag > 0 -> {
                    quadrantName = "I квадрант (a > 0, b > 0)"
                    phiFormula = "φ = arctg(b / a)"
                    phiCalc = "φ = arctg($bStr / $aStr) = $radStr рад (${degStr}°)"
                }
                real < 0 && imag > 0 -> {
                    quadrantName = "II квадрант (a < 0, b > 0)"
                    phiFormula = "φ = π + arctg(b / a) [или π - arctg(|b| / |a|)]"
                    phiCalc = "φ = π + arctg($bStr / $aStr) = $radStr рад (${degStr}°)"
                }
                real < 0 && imag < 0 -> {
                    quadrantName = "III квадрант (a < 0, b < 0)"
                    phiFormula = "φ = -π + arctg(b / a)"
                    phiCalc = "φ = -π + arctg($bStr / $aStr) = $radStr рад (${degStr}°)"
                }
                else -> {
                    quadrantName = "IV квадрант (a > 0, b < 0)"
                    phiFormula = "φ = arctg(b / a)"
                    phiCalc = "φ = arctg($bStr / $aStr) = $radStr рад (${degStr}°)"
                }
            }

            val nicePi = if (piFraction != null) " (точное значение: $piFraction)" else ""
            steps.add(
                MathStep(
                    title = "2. Вычисление аргумента (φ) — $quadrantName",
                    formula = phiFormula,
                    calculation = "$phiCalc$nicePi",
                    note = "Главное значение аргумента лежит в интервале (-π, π], что соответствует (-180°, 180°]."
                )
            )

            // Step 3: Final forms
            val unit = if (useJ) "j" else "i"
            val exp = toExponentialString(useJ = useJ, useDegrees = false, precision = precision)
            val trig = toTrigonometricString(useJ = useJ, useDegrees = false, precision = precision)
            val polar = toPolarNotationString(precision = precision)

            steps.add(
                MathStep(
                    title = "3. Запись в различных формах",
                    formula = "Показательная: z = r · e^($unit·φ)\nТригонометрическая: z = r · (cos φ + $unit · sin φ)",
                    calculation = "Показательная форма:\nz = $exp\n\nТригонометрическая форма:\nz = $trig\n\nПолярная инженерная нотация:\nz = $polar",
                    note = "Основано на формуле Эйлера: e^($unit·φ) = cos(φ) + $unit · sin(φ)."
                )
            )
        }

        return steps
    }

    fun getPolarToAlgebraicSteps(
        inputR: Double,
        inputPhi: Double,
        isDeg: Boolean,
        useJ: Boolean = false,
        precision: Int = 4
    ): List<MathStep> {
        val steps = mutableListOf<MathStep>()
        val unit = if (useJ) "j" else "i"
        val rStr = MathUtils.formatDouble(inputR, precision)

        val angleText = if (isDeg) "${MathUtils.formatDouble(inputPhi, 2)}°" else "${MathUtils.formatDouble(inputPhi, precision)} рад"
        val radValue = if (isDeg) Math.toRadians(inputPhi) else inputPhi

        // Step 1: Angles & Euler formula
        steps.add(
            MathStep(
                title = "1. Применение формулы Эйлера",
                formula = "z = r · e^($unit·φ) = r · (cos φ + $unit · sin φ) = r·cos φ + $unit·(r·sin φ)",
                calculation = "r = $rStr, φ = $angleText" + if (isDeg) " (= ${MathUtils.formatDouble(radValue, precision)} рад)" else "",
                note = "Вещественная часть: a = r · cos(φ), мнимая часть: b = r · sin(φ)."
            )
        )

        // Step 2: Compute Re and Im
        val cosVal = cos(radValue)
        val sinVal = sin(radValue)
        val aVal = inputR * cosVal
        val bVal = inputR * sinVal

        steps.add(
            MathStep(
                title = "2. Вычисление вещественной и мнимой частей",
                formula = "a = Re(z) = r · cos(φ)\nb = Im(z) = r · sin(φ)",
                calculation = "a = $rStr · cos($angleText) = $rStr · ${MathUtils.formatDouble(cosVal, 4)} = ${MathUtils.formatDouble(aVal, precision)}\n" +
                              "b = $rStr · sin($angleText) = $rStr · ${MathUtils.formatDouble(sinVal, 4)} = ${MathUtils.formatDouble(bVal, precision)}",
                note = "Тригонометрические значения вычислены с высокой точностью."
            )
        )

        // Step 3: Result
        val algResult = toAlgebraicString(useJ = useJ, precision = precision)
        steps.add(
            MathStep(
                title = "3. Запись в алгебраической форме",
                formula = "z = a + $unit·b",
                calculation = "z = $algResult",
                note = "Получено комплексное число в канонической алгебраической форме."
            )
        )

        return steps
    }
}
