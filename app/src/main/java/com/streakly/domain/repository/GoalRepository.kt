package com.streakly.domain.repository

import com.streakly.domain.model.ChecklistItem
import com.streakly.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getGoalsForDate(date: String): Flow<List<Goal>>
    fun getChecklistForDate(date: String): Flow<List<ChecklistItem>>
    suspend fun addGoal(goal: Goal): Long
    suspend fun updateGoal(goal: Goal)
    suspend fun toggleGoal(goalId: Long, completed: Boolean)
    suspend fun deleteGoal(goalId: Long)
    suspend fun addChecklistItem(item: ChecklistItem): Long
    suspend fun updateChecklistItem(item: ChecklistItem)
    suspend fun toggleChecklistItem(itemId: Long, completed: Boolean)
    suspend fun deleteChecklistItem(itemId: Long)
}
