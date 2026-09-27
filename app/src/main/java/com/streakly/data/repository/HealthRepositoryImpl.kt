package com.streakly.data.repository

import com.streakly.data.healthconnect.HealthConnectManager
import com.streakly.domain.model.DailyHealthSummary
import com.streakly.domain.model.HourlySteps
import com.streakly.domain.model.ReadinessScore
import com.streakly.domain.model.SleepSessionData
import com.streakly.domain.model.StepsByDay
import com.streakly.domain.model.VitalsSummary
import com.streakly.domain.model.WorkoutSession
import com.streakly.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRepositoryImpl @Inject constructor(
    private val healthConnectManager: HealthConnectManager
) : HealthRepository {

    override fun getTodayHealthSummary(): Flow<DailyHealthSummary> {
        return healthConnectManager.readDailySummary(LocalDate.now())
    }

    override fun getHourlyStepsToday(): Flow<List<HourlySteps>> {
        return healthConnectManager.readHourlySteps(LocalDate.now())
    }

    override fun getWeeklySteps(): Flow<List<StepsByDay>> {
        return healthConnectManager.readWeeklySteps()
    }

    override fun getRecentSleep(): Flow<SleepSessionData?> {
        return healthConnectManager.readRecentSleep()
    }

    override fun getSleepHistory(days: Int): Flow<List<SleepSessionData>> {
        return healthConnectManager.readSleepHistory(days)
    }

    override fun getVitalsSummary(): Flow<VitalsSummary> {
        return healthConnectManager.readVitals()
    }

    override fun getRecentWorkouts(): Flow<List<WorkoutSession>> {
        return healthConnectManager.readRecentWorkouts()
    }

    override fun getReadinessScore(): Flow<ReadinessScore> {
        return healthConnectManager.readReadinessScore()
    }

    override suspend fun syncHealthData() {
        // Triggers query or sync with health connect
    }
}
