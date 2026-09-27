package com.streakly.domain.repository

import com.streakly.domain.model.WaterIntake
import kotlinx.coroutines.flow.Flow

interface WaterRepository {
    fun getTodayWater(): Flow<WaterIntake>
    suspend fun addGlass()
    suspend fun removeGlass()
    suspend fun setGlasses(count: Int)
    suspend fun logWater(amountMl: Int)
    suspend fun getTodayTotalMl(date: String): Int
}
