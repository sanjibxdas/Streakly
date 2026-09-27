package com.streakly.data.repository

import android.content.Context
import com.streakly.data.local.dao.ChecklistItemDao
import com.streakly.data.local.dao.GoalDao
import com.streakly.data.local.entity.ChecklistItemEntity
import com.streakly.data.local.entity.GoalEntity
import com.streakly.domain.model.ChecklistItem
import com.streakly.domain.model.Goal
import com.streakly.domain.repository.GoalRepository
import com.streakly.util.NotificationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    private val checklistItemDao: ChecklistItemDao,
    @ApplicationContext private val context: Context
) : GoalRepository {

    override fun getGoalsForDate(date: String): Flow<List<Goal>> {
        return goalDao.getGoalsForDate(date).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getChecklistForDate(date: String): Flow<List<ChecklistItem>> {
        return checklistItemDao.getItemsForDate(date).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addGoal(goal: Goal): Long {
        val id = goalDao.insertGoal(goal.toEntity())
        if (!goal.notificationTime.isNullOrBlank()) {
            NotificationScheduler.scheduleGoalAlarm(context, id, goal.title, goal.notificationTime)
        }
        return id
    }

    override suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal.toEntity())
        if (!goal.notificationTime.isNullOrBlank() && !goal.completed) {
            NotificationScheduler.scheduleGoalAlarm(context, goal.id, goal.title, goal.notificationTime)
        } else {
            NotificationScheduler.cancelGoalAlarm(context, goal.id)
        }
    }

    override suspend fun toggleGoal(goalId: Long, completed: Boolean) {
        goalDao.toggleGoal(goalId, completed)
        if (completed) {
            NotificationScheduler.cancelGoalAlarm(context, goalId)
        }
    }

    override suspend fun deleteGoal(goalId: Long) {
        NotificationScheduler.cancelGoalAlarm(context, goalId)
        goalDao.deleteGoal(goalId)
    }

    override suspend fun addChecklistItem(item: ChecklistItem): Long {
        val id = checklistItemDao.insertItem(item.toEntity())
        if (!item.notificationTime.isNullOrBlank()) {
            NotificationScheduler.scheduleChecklistAlarm(context, id, item.title, item.notificationTime)
        }
        return id
    }

    override suspend fun updateChecklistItem(item: ChecklistItem) {
        checklistItemDao.updateItem(item.toEntity())
        if (!item.notificationTime.isNullOrBlank() && !item.completed) {
            NotificationScheduler.scheduleChecklistAlarm(context, item.id, item.title, item.notificationTime)
        } else {
            NotificationScheduler.cancelChecklistAlarm(context, item.id)
        }
    }

    override suspend fun toggleChecklistItem(itemId: Long, completed: Boolean) {
        checklistItemDao.toggleItem(itemId, completed)
        if (completed) {
            NotificationScheduler.cancelChecklistAlarm(context, itemId)
        }
    }

    override suspend fun deleteChecklistItem(itemId: Long) {
        NotificationScheduler.cancelChecklistAlarm(context, itemId)
        checklistItemDao.deleteItem(itemId)
    }

    private fun GoalEntity.toDomain() = Goal(
        id = id,
        title = title,
        date = date,
        completed = completed,
        notificationTime = notificationTime
    )

    private fun Goal.toEntity() = GoalEntity(
        id = id,
        title = title,
        date = date,
        completed = completed,
        notificationTime = notificationTime
    )

    private fun ChecklistItemEntity.toDomain() = ChecklistItem(
        id = id,
        title = title,
        date = date,
        completed = completed,
        notificationTime = notificationTime
    )

    private fun ChecklistItem.toEntity() = ChecklistItemEntity(
        id = id,
        title = title,
        date = date,
        completed = completed,
        notificationTime = notificationTime
    )
}
