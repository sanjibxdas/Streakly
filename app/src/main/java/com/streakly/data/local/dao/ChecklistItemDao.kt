package com.streakly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.streakly.data.local.entity.ChecklistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistItemDao {

    @Query("SELECT * FROM checklist_items WHERE date = :date ORDER BY id ASC")
    fun getItemsForDate(date: String): Flow<List<ChecklistItemEntity>>

    @Query("SELECT * FROM checklist_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): ChecklistItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ChecklistItemEntity): Long

    @Update
    suspend fun updateItem(item: ChecklistItemEntity)

    @Query("UPDATE checklist_items SET completed = :completed WHERE id = :id")
    suspend fun toggleItem(id: Long, completed: Boolean)

    @Query("DELETE FROM checklist_items WHERE id = :id")
    suspend fun deleteItem(id: Long)
}
