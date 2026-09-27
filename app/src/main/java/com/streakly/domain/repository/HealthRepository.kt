package com.streakly.domain.repository

import com.streakly.domain.model.DailyHealthSummary
import com.streakly.domain.model.HourlySteps
import com.streakly.domain.model.ReadinessScore
import com.streakly.domain.model.SleepSessionData
import com.streakly.domain.model.StepsByDay
import com.streakly.domain.model.VitalsSummary
import com.streakly.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

interface HealthRepository {
    fun getTodayHealthSummary(): Flow<DailyHealthSummary>
    fun getHourlyStepsToday(): Flow<List<HourlySteps>>
    fun getWeeklySteps(): Flow<List<StepsByDay>>
    fun getRecentSleep(): Flow<SleepSessionData?>
    fun getSleepHistory(days: Int): Flow<List<SleepSessionData>>
    fun getVitalsSummary(): Flow<VitalsSummary>
    fun getRecentWorkouts(): Flow<List<WorkoutSession>>
    fun getReadinessScore(): Flow<ReadinessScore>
    suspend fun syncHealthData()
}
