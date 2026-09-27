package com.streakly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.streakly.data.local.entity.WaterLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterLogDao {

    @Query("SELECT * FROM water_logs WHERE date = :date LIMIT 1")
    fun getWaterLogForDate(date: String): Flow<WaterLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(waterLog: WaterLogEntity): Long

    @Update
    suspend fun updateWaterLog(waterLog: WaterLogEntity)

    @Query("UPDATE water_logs SET glassesCount = :count, lastUpdated = :timestamp WHERE date = :date")
    suspend fun updateWaterCount(date: String, count: Int, timestamp: Long)

    @Query("DELETE FROM water_logs")
    suspend fun deleteAll()
}
