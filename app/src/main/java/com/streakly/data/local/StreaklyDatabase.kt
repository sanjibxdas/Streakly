package com.streakly.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.streakly.data.local.dao.AgentTaskDao
import com.streakly.data.local.dao.CachedHealthDataDao
import com.streakly.data.local.dao.ChecklistItemDao
import com.streakly.data.local.dao.GoalDao
import com.streakly.data.local.dao.HabitDao
import com.streakly.data.local.dao.HabitLogDao
import com.streakly.data.local.dao.JournalEntryDao
import com.streakly.data.local.dao.StreakCacheDao
import com.streakly.data.local.dao.WaterLogDao
import com.streakly.data.local.entity.AgentTaskEntity
import com.streakly.data.local.entity.CachedHealthDataEntity
import com.streakly.data.local.entity.ChecklistItemEntity
import com.streakly.data.local.entity.GoalEntity
import com.streakly.data.local.entity.HabitEntity
import com.streakly.data.local.entity.HabitLogEntity
import com.streakly.data.local.entity.JournalEntryEntity
import com.streakly.data.local.entity.StreakCacheEntity
import com.streakly.data.local.entity.WaterLogEntity

@Database(
    entities = [
        WaterLogEntity::class,
        JournalEntryEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        StreakCacheEntity::class,
        CachedHealthDataEntity::class,
        AgentTaskEntity::class,
        GoalEntity::class,
        ChecklistItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class StreaklyDatabase : RoomDatabase() {
    abstract fun waterLogDao(): WaterLogDao
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun streakCacheDao(): StreakCacheDao
    abstract fun cachedHealthDataDao(): CachedHealthDataDao
    abstract fun agentTaskDao(): AgentTaskDao
    abstract fun goalDao(): GoalDao
    abstract fun checklistItemDao(): ChecklistItemDao

    companion object {
        const val DATABASE_NAME = "streakly_db"
    }
}
