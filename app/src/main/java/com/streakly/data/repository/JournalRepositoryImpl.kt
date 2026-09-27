package com.streakly.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.streakly.data.local.dao.JournalEntryDao
import com.streakly.data.local.entity.JournalEntryEntity
import com.streakly.domain.model.HealthSnapshot
import com.streakly.domain.model.JournalEntry
import com.streakly.domain.model.Mood
import com.streakly.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalRepositoryImpl @Inject constructor(
    private val journalEntryDao: JournalEntryDao,
    private val gson: Gson
) : JournalRepository {

    private val stringListType = object : TypeToken<List<String>>() {}.type

    override fun getAllEntries(): Flow<List<JournalEntry>> {
        return journalEntryDao.getAllEntries().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun searchEntries(query: String): Flow<List<JournalEntry>> {
        return journalEntryDao.searchEntries(query).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getEntryById(id: Long): JournalEntry? {
        return journalEntryDao.getEntryById(id)?.toDomain()
    }

    override suspend fun saveEntry(entry: JournalEntry) {
        journalEntryDao.insertEntry(entry.toEntity())
    }

    override suspend fun deleteEntry(id: Long) {
        journalEntryDao.deleteEntry(id)
    }

    private fun JournalEntryEntity.toDomain(): JournalEntry {
        val tagsList: List<String> = try {
            gson.fromJson(tags, stringListType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        val snapshot = if (hasHealthSnapshot && snapshotSteps != null) {
            HealthSnapshot(
                steps = snapshotSteps,
                sleepScore = snapshotSleepScore,
                activeMinutes = snapshotActiveMin ?: 0,
                calories = snapshotCalories ?: 0
            )
        } else {
            null
        }

        return JournalEntry(
            id = id,
            title = title,
            body = body,
            mood = Mood.fromScore(mood),
            energyLevel = energyLevel,
            tags = tagsList,
            healthSnapshot = snapshot,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun JournalEntry.toEntity(): JournalEntryEntity {
        return JournalEntryEntity(
            id = id,
            title = title,
            body = body,
            mood = mood.score,
            energyLevel = energyLevel,
            tags = gson.toJson(tags),
            hasHealthSnapshot = healthSnapshot != null,
            snapshotSteps = healthSnapshot?.steps,
            snapshotSleepScore = healthSnapshot?.sleepScore,
            snapshotActiveMin = healthSnapshot?.activeMinutes,
            snapshotCalories = healthSnapshot?.calories,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
