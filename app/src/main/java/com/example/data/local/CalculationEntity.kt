package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculations")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: String, // "ALG_TO_POLAR" or "POLAR_TO_ALG"
    val realPart: Double,
    val imagPart: Double,
    val modulus: Double,
    val argumentRad: Double,
    val argumentDeg: Double,
    val formattedAlgebraic: String,
    val formattedExponential: String,
    val formattedTrigonometric: String,
    val formattedPolar: String,
    val isFavorite: Boolean = false
)
