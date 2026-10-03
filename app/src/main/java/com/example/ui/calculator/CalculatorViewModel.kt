package com.example.ui.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CalculationEntity
import com.example.data.repository.CalculationRepository
import com.example.model.ComplexNumber
import com.example.model.MathStep
import com.example.model.MathUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sqrt

enum class ConversionMode {
    ALGEBRAIC_TO_POLAR,
    POLAR_TO_ALGEBRAIC
}

enum class AngleUnit {
    DEGREES,
    RADIANS
}

data class CalculatorUiState(
    val mode: ConversionMode = ConversionMode.ALGEBRAIC_TO_POLAR,
    val inputReal: String = "3",
    val inputImag: String = "4",
    val inputR: String = "5",
    val inputAngle: String = "53.13",
    val angleUnit: AngleUnit = AngleUnit.DEGREES,
    val useJNotation: Boolean = false,
    val useDegreesInExp: Boolean = false,
    val precision: Int = 4,
    val currentComplex: ComplexNumber = ComplexNumber(3.0, 4.0),
    val steps: List<MathStep> = emptyList(),
    val isSaved: Boolean = false,
    val inputError: String? = null
)

class CalculatorViewModel(
    private val repository: CalculationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        recalculate()
    }

    fun setMode(newMode: ConversionMode) {
        if (_uiState.value.mode == newMode) return
        _uiState.update { it.copy(mode = newMode, isSaved = false) }
        recalculate()
    }

    fun setInputReal(text: String) {
        _uiState.update { it.copy(inputReal = text, isSaved = false) }
        recalculate()
    }

    fun setInputImag(text: String) {
        _uiState.update { it.copy(inputImag = text, isSaved = false) }
        recalculate()
    }

    fun setInputR(text: String) {
        _uiState.update { it.copy(inputR = text, isSaved = false) }
        recalculate()
    }

    fun setInputAngle(text: String) {
        _uiState.update { it.copy(inputAngle = text, isSaved = false) }
        recalculate()
    }

    fun setAngleUnit(unit: AngleUnit) {
        val current = _uiState.value
        if (current.angleUnit == unit) return

        // Convert the angle input text when switching units
        val angleVal = parseNumber(current.inputAngle) ?: 0.0
        val convertedAngle = if (unit == AngleUnit.DEGREES) {
            // From Rad to Deg
            MathUtils.formatDouble(Math.toDegrees(angleVal), 2)
        } else {
            // From Deg to Rad
            MathUtils.formatDouble(Math.toRadians(angleVal), 4)
        }

        _uiState.update {
            it.copy(
                angleUnit = unit,
                inputAngle = convertedAngle,
                isSaved = false
            )
        }
        recalculate()
    }

    fun toggleJNotation() {
        _uiState.update { it.copy(useJNotation = !it.useJNotation) }
        recalculate()
    }

    fun toggleDegreesInExp() {
        _uiState.update { it.copy(useDegreesInExp = !it.useDegreesInExp) }
        recalculate()
    }

    fun setPrecision(precision: Int) {
        _uiState.update { it.copy(precision = precision) }
        recalculate()
    }

    fun updateFromPlane(newRe: Double, newIm: Double) {
        val current = _uiState.value
        val complex = ComplexNumber(newRe, newIm)
        val p = current.precision

        if (current.mode == ConversionMode.ALGEBRAIC_TO_POLAR) {
            _uiState.update {
                it.copy(
                    inputReal = MathUtils.formatDouble(newRe, p),
                    inputImag = MathUtils.formatDouble(newIm, p),
                    currentComplex = complex,
                    isSaved = false
                )
            }
        } else {
            val r = complex.modulus
            val angle = if (current.angleUnit == AngleUnit.DEGREES) complex.argumentDeg else complex.argumentRad
            _uiState.update {
                it.copy(
                    inputR = MathUtils.formatDouble(r, p),
                    inputAngle = MathUtils.formatDouble(angle, if (current.angleUnit == AngleUnit.DEGREES) 2 else 4),
                    currentComplex = complex,
                    isSaved = false
                )
            }
        }
        recalculate()
    }

    fun loadFromHistory(re: Double, im: Double) {
        val complex = ComplexNumber(re, im)
        val p = _uiState.value.precision
        _uiState.update {
            it.copy(
                inputReal = MathUtils.formatDouble(re, p),
                inputImag = MathUtils.formatDouble(im, p),
                inputR = MathUtils.formatDouble(complex.modulus, p),
                inputAngle = MathUtils.formatDouble(
                    if (it.angleUnit == AngleUnit.DEGREES) complex.argumentDeg else complex.argumentRad,
                    if (it.angleUnit == AngleUnit.DEGREES) 2 else 4
                ),
                currentComplex = complex,
                isSaved = true
            )
        }
        recalculate()
    }

    fun applyPreset(re: Double, im: Double) {
        val complex = ComplexNumber(re, im)
        val p = _uiState.value.precision
        _uiState.update {
            it.copy(
                inputReal = MathUtils.formatDouble(re, p),
                inputImag = MathUtils.formatDouble(im, p),
                inputR = MathUtils.formatDouble(complex.modulus, p),
                inputAngle = MathUtils.formatDouble(
                    if (it.angleUnit == AngleUnit.DEGREES) complex.argumentDeg else complex.argumentRad,
                    if (it.angleUnit == AngleUnit.DEGREES) 2 else 4
                ),
                currentComplex = complex,
                isSaved = false
            )
        }
        recalculate()
    }

    fun negateReal() {
        val cur = parseNumber(_uiState.value.inputReal) ?: 0.0
        setInputReal(MathUtils.formatDouble(-cur, _uiState.value.precision))
    }

    fun negateImag() {
        val cur = parseNumber(_uiState.value.inputImag) ?: 0.0
        setInputImag(MathUtils.formatDouble(-cur, _uiState.value.precision))
    }

    fun negateAngle() {
        val cur = parseNumber(_uiState.value.inputAngle) ?: 0.0
        setInputAngle(MathUtils.formatDouble(-cur, 2))
    }

    fun applyPiFractionToAngle(factor: Double) {
        val rad = factor * PI
        if (_uiState.value.angleUnit == AngleUnit.DEGREES) {
            setInputAngle(MathUtils.formatDouble(Math.toDegrees(rad), 2))
        } else {
            setInputAngle(MathUtils.formatDouble(rad, 4))
        }
    }

    fun saveToHistory() {
        val state = _uiState.value
        val c = state.currentComplex
        val entity = CalculationEntity(
            mode = if (state.mode == ConversionMode.ALGEBRAIC_TO_POLAR) "ALG_TO_POLAR" else "POLAR_TO_ALG",
            realPart = c.real,
            imagPart = c.imag,
            modulus = c.modulus,
            argumentRad = c.argumentRad,
            argumentDeg = c.argumentDeg,
            formattedAlgebraic = c.toAlgebraicString(useJ = state.useJNotation, precision = state.precision),
            formattedExponential = c.toExponentialString(useJ = state.useJNotation, useDegrees = state.useDegreesInExp, precision = state.precision),
            formattedTrigonometric = c.toTrigonometricString(useJ = state.useJNotation, useDegrees = state.useDegreesInExp, precision = state.precision),
            formattedPolar = c.toPolarNotationString(precision = state.precision)
        )

        viewModelScope.launch {
            repository.saveCalculation(entity)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    private fun recalculate() {
        val state = _uiState.value
        try {
            if (state.mode == ConversionMode.ALGEBRAIC_TO_POLAR) {
                val re = parseNumber(state.inputReal) ?: 0.0
                val im = parseNumber(state.inputImag) ?: 0.0
                val complex = ComplexNumber(re, im)
                val steps = complex.getAlgebraicToPolarSteps(
                    useJ = state.useJNotation,
                    precision = state.precision
                )
                _uiState.update {
                    it.copy(
                        currentComplex = complex,
                        steps = steps,
                        inputError = null
                    )
                }
            } else {
                val r = parseNumber(state.inputR) ?: 0.0
                val angle = parseNumber(state.inputAngle) ?: 0.0
                val isDeg = state.angleUnit == AngleUnit.DEGREES
                val complex = if (isDeg) {
                    ComplexNumber.fromPolarDeg(r, angle)
                } else {
                    ComplexNumber.fromPolarRad(r, angle)
                }
                val steps = complex.getPolarToAlgebraicSteps(
                    inputR = r,
                    inputPhi = angle,
                    isDeg = isDeg,
                    useJ = state.useJNotation,
                    precision = state.precision
                )
                _uiState.update {
                    it.copy(
                        currentComplex = complex,
                        steps = steps,
                        inputError = null
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(inputError = e.localizedMessage) }
        }
    }

    private fun parseNumber(text: String): Double? {
        val clean = text.trim().replace(',', '.')
        if (clean.isEmpty() || clean == "-" || clean == "+") return 0.0

        // Handle simple square root expressions like sqrt(3) or √3
        if (clean.startsWith("√") || clean.startsWith("sqrt", ignoreCase = true)) {
            val inner = clean.removePrefix("√").removePrefix("sqrt").removePrefix("(").removeSuffix(")").trim()
            val innerVal = inner.toDoubleOrNull()
            if (innerVal != null && innerVal >= 0) {
                return sqrt(innerVal)
            }
        }

        // Handle negative root e.g. -√3
        if (clean.startsWith("-√") || clean.startsWith("-sqrt", ignoreCase = true)) {
            val inner = clean.removePrefix("-√").removePrefix("-sqrt").removePrefix("(").removeSuffix(")").trim()
            val innerVal = inner.toDoubleOrNull()
            if (innerVal != null && innerVal >= 0) {
                return -sqrt(innerVal)
            }
        }

        return clean.toDoubleOrNull()
    }
}
