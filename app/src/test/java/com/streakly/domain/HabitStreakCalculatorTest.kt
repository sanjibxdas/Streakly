package com.streakly.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class HabitStreakCalculatorTest {

    private fun calculateStreak(completedDates: Set<String>, today: LocalDate = LocalDate.now()): Int {
        var streak = 0
        var checkDate = today

        // If today is completed, start from today. If not, check if yesterday was completed.
        if (completedDates.contains(checkDate.toString())) {
            streak++
            checkDate = checkDate.minusDays(1)
        } else if (completedDates.contains(checkDate.minusDays(1).toString())) {
            checkDate = checkDate.minusDays(1)
        } else {
            return 0
        }

        while (completedDates.contains(checkDate.toString())) {
            streak++
            checkDate = checkDate.minusDays(1)
        }

        return streak
    }

    @Test
    fun calculateStreak_consecutiveDays_returnsCorrectStreak() {
        val today = LocalDate.of(2026, 9, 27)
        val completed = setOf(
            "2026-09-27",
            "2026-09-26",
            "2026-09-25",
            "2026-09-24"
        )
        val streak = calculateStreak(completed, today)
        assertEquals(4, streak)
    }

    @Test
    fun calculateStreak_missedToday_retainsYesterdayStreak() {
        val today = LocalDate.of(2026, 9, 27)
        val completed = setOf(
            "2026-09-26",
            "2026-09-25"
        )
        val streak = calculateStreak(completed, today)
        assertEquals(2, streak)
    }

    @Test
    fun calculateStreak_brokenStreak_returnsZero() {
        val today = LocalDate.of(2026, 9, 27)
        val completed = setOf(
            "2026-09-24",
            "2026-09-23"
        )
        val streak = calculateStreak(completed, today)
        assertEquals(0, streak)
    }

    @Test
    fun calculateStreak_emptyHistory_returnsZero() {
        val streak = calculateStreak(emptySet())
        assertEquals(0, streak)
    }
}
