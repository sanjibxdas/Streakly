package com.streakly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val body: String,
    val mood: Int,
    val energyLevel: Int,
    val tags: String,
    val hasHealthSnapshot: Boolean,
    val snapshotSteps: Int?,
    val snapshotSleepScore: Int?,
    val snapshotActiveMin: Int?,
    val snapshotCalories: Int?,
    val createdAt: Long,
    val updatedAt: Long
)
