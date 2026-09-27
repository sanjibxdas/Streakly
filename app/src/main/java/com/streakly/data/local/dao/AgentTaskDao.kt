package com.streakly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.streakly.data.local.entity.AgentTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentTaskDao {

    @Query("SELECT * FROM agent_tasks WHERE date = :date ORDER BY time ASC")
    fun getTasksForDate(date: String): Flow<List<AgentTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: AgentTaskEntity): Long

    @Update
    suspend fun updateTask(task: AgentTaskEntity)

    @Query("UPDATE agent_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleTask(id: Long, completed: Boolean)

    @Query("DELETE FROM agent_tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)
}
