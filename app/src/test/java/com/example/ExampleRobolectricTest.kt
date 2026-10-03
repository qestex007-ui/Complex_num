package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.CalculationEntity
import com.example.data.repository.CalculationRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: CalculationRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CalculationRepository(db.calculationDao())
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testStringResource() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Комплексные числа", appName)
    }

    @Test
    fun testRoomDatabaseHistoryOperations() = runBlocking {
        val entity = CalculationEntity(
            mode = "ALG_TO_POLAR",
            realPart = 3.0,
            imagPart = 4.0,
            modulus = 5.0,
            argumentRad = 0.9273,
            argumentDeg = 53.13,
            formattedAlgebraic = "3 + 4i",
            formattedExponential = "5 e^(i · 0.9273 rad)",
            formattedTrigonometric = "5 · (cos(53.13°) + i · sin(53.13°))",
            formattedPolar = "5 ∠ 53.13°",
            isFavorite = false
        )

        val id = repository.saveCalculation(entity)
        assertTrue(id > 0)

        val items = repository.allCalculations.first()
        assertEquals(1, items.size)
        assertEquals("3 + 4i", items[0].formattedAlgebraic)

        repository.toggleFavorite(id, true)
        val favs = repository.favoriteCalculations.first()
        assertEquals(1, favs.size)

        repository.deleteCalculation(id)
        val empty = repository.allCalculations.first()
        assertEquals(0, empty.size)
    }
}
