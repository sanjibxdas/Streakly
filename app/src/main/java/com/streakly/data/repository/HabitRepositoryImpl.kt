package com.streakly.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.streakly.data.local.dao.HabitDao
import com.streakly.data.local.dao.HabitLogDao
import com.streakly.data.local.entity.HabitEntity
import com.streakly.data.local.entity.HabitLogEntity
import com.streakly.domain.model.Habit
import com.streakly.domain.model.HabitDayStatus
import com.streakly.domain.model.HabitWithCompletion
import com.streakly.domain.model.TimeOfDay
import com.streakly.domain.repository.HabitRepository
import com.streakly.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao,
    private val gson: Gson
) : HabitRepository {

    private val stringListType = object : TypeToken<List<String>>() {}.type

    override fun getHabits(): Flow<List<HabitWithCompletion>> {
        return habitDao.getAllHabits().map { entities ->
            val todayIso = DateUtils.getTodayIso()
            val past7DateIsos = DateUtils.getPastNDays(7, includeToday = true)

            entities.map { entity ->
                val habit = entity.toDomain()
                val logs = habitLogDao.getAllLogsForHabitSync(habit.id)
                val logMap = logs.associateBy { it.date }

                val isCompletedToday = logMap[todayIso]?.completed == true

                val past7Days = past7DateIsos.map { dateIso ->
                    HabitDayStatus(
                        date = dateIso,
                        dayLetter = DateUtils.getDayOfWeekLetter(dateIso),
                        isCompleted = logMap[dateIso]?.completed == true
                    )
                }

                val currentStreak = calculateCurrentStreak(logs, todayIso)
                val longestStreak = calculateLongestStreak(logs)

                HabitWithCompletion(
                    habit = habit,
                    isCompletedToday = isCompletedToday,
                    currentStreak = currentStreak,
                    longestStreak = longestStreak.coerceAtLeast(currentStreak),
                    past7Days = past7Days
                )
            }
        }
    }

    override suspend fun getHabitById(id: Long): Habit? {
        return habitDao.getHabitById(id)?.toDomain()
    }

    override suspend fun createHabit(habit: Habit): Long {
        val entity = HabitEntity(
            name = habit.name,
            iconName = habit.iconName,
            colorHex = habit.colorHex,
            frequencyJson = gson.toJson(habit.frequency),
            timeOfDay = habit.timeOfDay.name,
            linkedMetric = habit.linkedMetric,
            createdAt = habit.createdAt
        )
        return habitDao.insertHabit(entity)
    }

    override suspend fun updateHabit(habit: Habit) {
        val entity = HabitEntity(
            id = habit.id,
            name = habit.name,
            iconName = habit.iconName,
            colorHex = habit.colorHex,
            frequencyJson = gson.toJson(habit.frequency),
            timeOfDay = habit.timeOfDay.name,
            linkedMetric = habit.linkedMetric,
            createdAt = habit.createdAt
        )
        habitDao.updateHabit(entity)
    }

    override suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabit(habitId)
    }

    override suspend fun toggleHabitCompletion(habitId: Long, date: String): Boolean {
        val existing = habitLogDao.getLog(habitId, date)
        val now = System.currentTimeMillis()
        return if (existing == null) {
            habitLogDao.insertLog(
                HabitLogEntity(
                    habitId = habitId,
                    date = date,
                    completed = true,
                    completedAt = now
                )
            )
            true
        } else {
            val newCompleted = !existing.completed
            habitLogDao.updateLog(
                existing.copy(
                    completed = newCompleted,
                    completedAt = if (newCompleted) now else null
                )
            )
            newCompleted
        }
    }

    private fun HabitEntity.toDomain(): Habit {
        val frequencyList: List<String> = try {
            gson.fromJson(frequencyJson, stringListType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        val tod = try {
            TimeOfDay.valueOf(timeOfDay)
        } catch (e: Exception) {
            TimeOfDay.ANYTIME
        }
        return Habit(
            id = id,
            name = name,
            iconName = iconName,
            colorHex = colorHex,
            frequency = frequencyList,
            timeOfDay = tod,
            linkedMetric = linkedMetric,
            createdAt = createdAt
        )
    }

    companion object {
        fun calculateCurrentStreak(logs: List<HabitLogEntity>, todayIso: String): Int {
            val completedDates = logs.filter { it.completed }.map { it.date }.toSet()
            var checkDate = LocalDate.parse(todayIso, DateTimeFormatter.ISO_LOCAL_DATE)

            // If not completed today, streak can still be alive if completed yesterday
            if (!completedDates.contains(checkDate.format(DateTimeFormatter.ISO_LOCAL_DATE))) {
                checkDate = checkDate.minusDays(1)
            }

            var streak = 0
            while (completedDates.contains(checkDate.format(DateTimeFormatter.ISO_LOCAL_DATE))) {
                streak++
                checkDate = checkDate.minusDays(1)
            }
            return streak
        }

        fun calculateLongestStreak(logs: List<HabitLogEntity>): Int {
            val sortedDates = logs
                .filter { it.completed }
                .map { LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE) }
                .distinct()
                .sorted()

            if (sortedDates.isEmpty()) return 0

            var maxStreak = 1
            var currentRun = 1

            for (i in 1 until sortedDates.size) {
                if (sortedDates[i] == sortedDates[i - 1].plusDays(1)) {
                    currentRun++
                    if (currentRun > maxStreak) {
                        maxStreak = currentRun
                    }
                } else if (sortedDates[i] != sortedDates[i - 1]) {
                    currentRun = 1
                }
            }
            return maxStreak
        }
    }
}
