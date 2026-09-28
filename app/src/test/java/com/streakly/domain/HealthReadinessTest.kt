package com.streakly.domain

import com.streakly.domain.model.ReadinessScore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthReadinessTest {

    private fun calculateReadiness(
        sleepMinutes: Int,
        targetSleepMinutes: Int = 480,
        steps: Int,
        targetSteps: Int = 10000,
        restingHr: Int = 60
    ): ReadinessScore {
        val sleepRatio = (sleepMinutes.toFloat() / targetSleepMinutes).coerceIn(0f, 1.2f)
        val sleepFactor = (sleepRatio * 100).coerceAtMost(100f).toInt()

        val stepRatio = (steps.toFloat() / targetSteps).coerceIn(0f, 1.2f)
        val activityFactor = (stepRatio * 100).coerceAtMost(100f).toInt()

        val restingHrFactor = when {
            restingHr <= 55 -> 100
            restingHr <= 65 -> 90
            restingHr <= 75 -> 75
            restingHr <= 85 -> 60
            else -> 45
        }

        val totalScore = ((sleepFactor * 0.40f) + (activityFactor * 0.35f) + (restingHrFactor * 0.25f)).toInt()

        val description = when {
            totalScore >= 85 -> "Prime condition. Optimal for intense workouts and focus."
            totalScore >= 70 -> "Good recovery. Maintain steady momentum today."
            else -> "Prioritize rest, recovery, and adequate hydration."
        }

        return ReadinessScore(
            score = totalScore,
            description = description,
            sleepFactor = sleepFactor,
            activityFactor = activityFactor,
            restingHrFactor = restingHrFactor
        )
    }

    @Test
    fun calculateReadiness_optimalInputs_returnsHighScore() {
        val readiness = calculateReadiness(
            sleepMinutes = 480,
            targetSleepMinutes = 480,
            steps = 10000,
            targetSteps = 10000,
            restingHr = 58
        )
        // 100*0.4 + 100*0.35 + 90*0.25 = 40 + 35 + 22.5 = 97
        assertEquals(97, readiness.score)
        assertTrue(readiness.score >= 85)
        assertTrue(readiness.description.contains("Prime condition"))
    }

    @Test
    fun calculateReadiness_poorSleep_reducesReadiness() {
        val readiness = calculateReadiness(
            sleepMinutes = 240, // 50%
            targetSleepMinutes = 480,
            steps = 3000, // 30%
            targetSteps = 10000,
            restingHr = 80 // 60
        )
        // 50*0.4 + 30*0.35 + 60*0.25 = 20 + 10.5 + 15 = 45
        assertEquals(45, readiness.score)
        assertTrue(readiness.score < 70)
        assertTrue(readiness.description.contains("Prioritize rest"))
    }
}
