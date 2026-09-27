package com.streakly.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DateUtilsTest {

    @Test
    fun testGetTodayIsoFormat() {
        val todayIso = DateUtils.getTodayIso()
        assertNotNull(todayIso)
        assertTrue(todayIso.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun testFormatDuration() {
        assertEquals("1h 15m", DateUtils.formatDuration(75))
        assertEquals("2h", DateUtils.formatDuration(120))
        assertEquals("45m", DateUtils.formatDuration(45))
    }

    @Test
    fun testFormatSteps() {
        val formatted = DateUtils.formatSteps(10000)
        assertTrue(formatted.contains("10") && formatted.contains("000"))
    }

    @Test
    fun testFormatDistance() {
        val formatted = DateUtils.formatDistance(5200.0)
        assertEquals("5.2 km", formatted)
    }

    @Test
    fun testGetGreeting() {
        val greeting = DateUtils.getGreeting("Alex")
        assertTrue(greeting.contains("Alex"))
    }

    @Test
    fun testGetPastNDays() {
        val pastDays = DateUtils.getPastNDays(7)
        assertEquals(7, pastDays.size)
        assertTrue(DateUtils.isToday(pastDays.last()))
    }
}
