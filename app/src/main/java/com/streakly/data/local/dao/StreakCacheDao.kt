package com.streakly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.streakly.data.local.entity.StreakCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakCacheDao {

    @Query("SELECT * FROM streak_cache WHERE type = :type LIMIT 1")
    fun getStreak(type: String): Flow<StreakCacheEntity?>

    @Query("SELECT * FROM streak_cache")
    fun getAllStreaks(): Flow<List<StreakCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStreak(streak: StreakCacheEntity)
}
