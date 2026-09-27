package com.streakly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak_cache")
data class StreakCacheEntity(
    @PrimaryKey
    val type: String,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastCompletedDate: String,
    val updatedAt: Long
)
