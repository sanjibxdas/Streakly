package com.streakly.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.streakly.worker.AlarmReceiver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object NotificationScheduler {

    const val ACTION_GOAL = "com.streakly.ACTION_GOAL_REMINDER"
    const val ACTION_CHECKLIST = "com.streakly.ACTION_CHECKLIST_REMINDER"
    const val ACTION_NIGHT = "com.streakly.ACTION_NIGHT_REMINDER"

    const val EXTRA_ID = "extra_id"
    const val EXTRA_TITLE = "extra_title"

    fun scheduleGoalAlarm(context: Context, goalId: Long, title: String, time: String) {
        val triggerMillis = calculateTriggerMillis(time)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_GOAL
            putExtra(EXTRA_ID, goalId)
            putExtra(EXTRA_TITLE, title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (NotificationHelper.NOTIF_ID_GOAL_BASE + goalId).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarm(context, triggerMillis, pendingIntent)
    }

    fun cancelGoalAlarm(context: Context, goalId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_GOAL
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (NotificationHelper.NOTIF_ID_GOAL_BASE + goalId).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.cancel(pendingIntent)
    }

    fun scheduleChecklistAlarm(context: Context, itemId: Long, title: String, time: String) {
        val triggerMillis = calculateTriggerMillis(time)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_CHECKLIST
            putExtra(EXTRA_ID, itemId)
            putExtra(EXTRA_TITLE, title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (NotificationHelper.NOTIF_ID_CHECKLIST_BASE + itemId).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarm(context, triggerMillis, pendingIntent)
    }

    fun cancelChecklistAlarm(context: Context, itemId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_CHECKLIST
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (NotificationHelper.NOTIF_ID_CHECKLIST_BASE + itemId).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.cancel(pendingIntent)
    }

    fun scheduleNightReminder(context: Context, time: String) {
        val triggerMillis = calculateTriggerMillis(time)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_NIGHT
            putExtra(EXTRA_TITLE, "Bedtime Wind-down")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NotificationHelper.NOTIF_ID_BEDTIME,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarm(context, triggerMillis, pendingIntent)
    }

    private fun setAlarm(context: Context, triggerMillis: Long, pendingIntent: PendingIntent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            // Fallback for security exception
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
        }
    }

    fun calculateTriggerMillis(time: String): Long {
        val parts = time.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 21
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

        val now = LocalDateTime.now()
        var scheduledTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute))
        if (scheduledTime.isBefore(now)) {
            scheduledTime = scheduledTime.plusDays(1)
        }
        return scheduledTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
