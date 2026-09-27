package com.streakly.domain.repository

import com.streakly.domain.model.Habit
import com.streakly.domain.model.HabitWithCompletion
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getHabits(): Flow<List<HabitWithCompletion>>
    suspend fun getHabitById(id: Long): Habit?
    suspend fun createHabit(habit: Habit): Long
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habitId: Long)
    suspend fun toggleHabitCompletion(habitId: Long, date: String): Boolean
}
