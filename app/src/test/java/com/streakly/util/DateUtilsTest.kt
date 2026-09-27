package com.streakly.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DateUtilsTest {

    @Test
    fun formatDuration_hoursAndMinutes_formattedCorrectly() {
        val formatted = DateUtils.formatDuration(135)
        assertEquals("2h 15m", formatted)
    }

    @Test
    fun formatDuration_hoursOnly_formattedCorrectly() {
        val formatted = DateUtils.formatDuration(120)
        assertEquals("2h", formatted)
    }

    @Test
    fun formatDuration_minutesOnly_formattedCorrectly() {
        val formatted = DateUtils.formatDuration(45)
        assertEquals("45m", formatted)
    }

    @Test
    fun getPastNDays_returnsRequestedCount() {
        val days = DateUtils.getPastNDays(7, includeToday = true)
        assertEquals(7, days.size)
        assertTrue(DateUtils.isToday(days.last()))
    }

    @Test
    fun isToday_matchesCurrentDate() {
        val today = DateUtils.getTodayIso()
        assertTrue(DateUtils.isToday(today))
        assertFalse(DateUtils.isToday("2020-01-01"))
    }
}
