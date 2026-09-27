package com.streakly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.streakly.data.local.entity.CachedHealthDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedHealthDataDao {

    @Query("SELECT * FROM cached_health_data WHERE date = :date LIMIT 1")
    fun getHealthDataForDate(date: String): Flow<CachedHealthDataEntity?>

    @Query("SELECT * FROM cached_health_data WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getHealthDataBetween(startDate: String, endDate: String): Flow<List<CachedHealthDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHealthData(data: CachedHealthDataEntity)
}
