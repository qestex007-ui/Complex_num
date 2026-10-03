package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Query("SELECT * FROM calculations ORDER BY timestamp DESC")
    fun getAllCalculations(): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculations WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteCalculations(): Flow<List<CalculationEntity>>

    @Query("""
        SELECT * FROM calculations 
        WHERE formattedAlgebraic LIKE '%' || :query || '%' 
           OR formattedExponential LIKE '%' || :query || '%'
           OR formattedTrigonometric LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun searchCalculations(query: String): Flow<List<CalculationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(entity: CalculationEntity): Long

    @Update
    suspend fun updateCalculation(entity: CalculationEntity)

    @Query("UPDATE calculations SET isFavorite = :isFav WHERE id = :id")
    suspend fun toggleFavorite(id: Long, isFav: Boolean)

    @Query("DELETE FROM calculations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM calculations")
    suspend fun clearAll()
}
