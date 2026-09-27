package com.streakly.domain.model

data class StepsByDay(
    val date: String,
    val count: Int,
    val target: Int = 10000
)

data class HourlySteps(
    val hour: Int,
    val count: Int
)

data class HeartRateSample(
    val timestamp: Long,
    val bpm: Int
)

data class SleepStage(
    val stageName: String,
    val durationMinutes: Int,
    val percentage: Float
)

data class SleepSessionData(
    val date: String,
    val durationMinutes: Int,
    val score: Int,
    val deepMinutes: Int,
    val remMinutes: Int,
    val lightMinutes: Int,
    val awakeMinutes: Int,
    val stages: List<SleepStage> = emptyList()
)

data class ReadinessScore(
    val score: Int,
    val description: String,
    val sleepFactor: Int,
    val activityFactor: Int,
    val restingHrFactor: Int
)

data class WorkoutSession(
    val id: String,
    val title: String,
    val type: String,
    val durationMinutes: Int,
    val calories: Int,
    val date: String,
    val avgHeartRate: Int? = null
)

data class VitalsSummary(
    val restingHeartRate: Int?,
    val oxygenSaturation: Int?,
    val heartRateVariability: Double?,
    val respiratoryRate: Double?,
    val vo2Max: Double?
)

data class DailyHealthSummary(
    val date: String,
    val steps: Int,
    val targetSteps: Int = 10000,
    val activeMinutes: Int,
    val targetActiveMinutes: Int = 30,
    val caloriesBurned: Int,
    val distanceMeters: Double,
    val sleepDurationMinutes: Int? = null,
    val sleepScore: Int? = null,
    val restingHeartRate: Int? = null,
    val readinessScore: ReadinessScore? = null
)
