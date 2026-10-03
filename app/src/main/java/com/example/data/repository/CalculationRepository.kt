package com.example.data.repository

import com.example.data.local.CalculationDao
import com.example.data.local.CalculationEntity
import kotlinx.coroutines.flow.Flow

class CalculationRepository(private val calculationDao: CalculationDao) {
    val allCalculations: Flow<List<CalculationEntity>> = calculationDao.getAllCalculations()
    val favoriteCalculations: Flow<List<CalculationEntity>> = calculationDao.getFavoriteCalculations()

    fun searchCalculations(query: String): Flow<List<CalculationEntity>> {
        return calculationDao.searchCalculations(query)
    }

    suspend fun saveCalculation(entity: CalculationEntity): Long {
        return calculationDao.insertCalculation(entity)
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        calculationDao.toggleFavorite(id, isFavorite)
    }

    suspend fun deleteCalculation(id: Long) {
        calculationDao.deleteById(id)
    }

    suspend fun clearAllCalculations() {
        calculationDao.clearAll()
    }
}
