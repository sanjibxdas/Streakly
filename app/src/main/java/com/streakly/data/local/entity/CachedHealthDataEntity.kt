package com.streakly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_health_data")
data class CachedHealthDataEntity(
    @PrimaryKey
    val date: String,
    val stepsTotal: Int,
    val activeMinutes: Int,
    val caloriesBurned: Int,
    val distanceMeters: Double,
    val sleepScore: Int?,
    val restingHR: Int?,
    val syncedAt: Long
)
