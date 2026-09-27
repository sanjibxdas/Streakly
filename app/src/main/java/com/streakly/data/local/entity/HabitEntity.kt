package com.streakly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val frequencyJson: String,
    val timeOfDay: String,
    val linkedMetric: String?,
    val createdAt: Long
)
