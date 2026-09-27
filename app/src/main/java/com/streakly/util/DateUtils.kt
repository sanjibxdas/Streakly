package com.streakly.util

import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {

    private val ISO_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val DISPLAY_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault())
    private val SHORT_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())

    fun getTodayIso(): String {
        return LocalDate.now().format(ISO_DATE_FORMATTER)
    }

    fun getDisplayDate(dateIso: String = getTodayIso()): String {
        return try {
            val date = LocalDate.parse(dateIso, ISO_DATE_FORMATTER)
            date.format(DISPLAY_DATE_FORMATTER)
        } catch (e: Exception) {
            dateIso
        }
    }

    fun getShortDisplayDate(dateIso: String): String {
        return try {
            val date = LocalDate.parse(dateIso, ISO_DATE_FORMATTER)
            date.format(SHORT_DATE_FORMATTER)
        } catch (e: Exception) {
            dateIso
        }
    }

    fun getDayOfWeekShort(dateIso: String): String {
        return try {
            val date = LocalDate.parse(dateIso, ISO_DATE_FORMATTER)
            date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        } catch (e: Exception) {
            ""
        }
    }

    fun getDayOfWeekLetter(dateIso: String): String {
        return try {
            val date = LocalDate.parse(dateIso, ISO_DATE_FORMATTER)
            date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())
        } catch (e: Exception) {
            ""
        }
    }

    fun getGreeting(name: String = "there"): String {
        val hour = LocalTime.now().hour
        val greetingPrefix = when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Good night"
        }
        return if (name.isNotBlank()) "$greetingPrefix, $name" else greetingPrefix
    }

    fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return when {
            hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}m"
            hours > 0 -> "${hours}h"
            else -> "${remainingMinutes}m"
        }
    }

    fun formatDurationMillis(millis: Long): String {
        val totalMinutes = (millis / (1000 * 60)).toInt()
        return formatDuration(totalMinutes)
    }

    fun formatSteps(steps: Int): String {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(steps)
    }

    fun formatCalories(calories: Int): String {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(calories)
    }

    fun formatDistance(meters: Double): String {
        val km = meters / 1000.0
        return String.format(Locale.getDefault(), "%.1f km", km)
    }

    fun getPastNDays(count: Int, includeToday: Boolean = true): List<String> {
        val dates = mutableListOf<String>()
        val today = LocalDate.now()
        val startIndex = if (includeToday) 0 else 1
        for (i in (count - 1 + startIndex) downTo startIndex) {
            dates.add(today.minusDays(i.toLong()).format(ISO_DATE_FORMATTER))
        }
        return dates
    }

    fun isToday(dateIso: String): Boolean {
        return dateIso == getTodayIso()
    }
}
