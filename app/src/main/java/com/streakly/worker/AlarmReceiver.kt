package com.streakly.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.streakly.R
import com.streakly.util.NotificationHelper
import com.streakly.util.NotificationScheduler

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(NotificationScheduler.EXTRA_TITLE) ?: ""
        val id = intent.getLongExtra(NotificationScheduler.EXTRA_ID, 0L)

        when (intent.action) {
            NotificationScheduler.ACTION_GOAL -> {
                NotificationHelper.showNotification(
                    context = context,
                    id = (NotificationHelper.NOTIF_ID_GOAL_BASE + id).toInt(),
                    title = context.getString(R.string.goal_reminder_title),
                    message = title.ifBlank { "Don't forget to complete your scheduled goal today!" },
                    channelId = NotificationHelper.CHANNEL_ID_ALARMS
                )
            }
            NotificationScheduler.ACTION_CHECKLIST -> {
                NotificationHelper.showNotification(
                    context = context,
                    id = (NotificationHelper.NOTIF_ID_CHECKLIST_BASE + id).toInt(),
                    title = "Checklist Reminder",
                    message = title.ifBlank { "Task scheduled for right now." },
                    channelId = NotificationHelper.CHANNEL_ID_ALARMS
                )
            }
            NotificationScheduler.ACTION_NIGHT -> {
                NotificationHelper.showNotification(
                    context = context,
                    id = NotificationHelper.NOTIF_ID_BEDTIME,
                    title = context.getString(R.string.bedtime_reminder_title),
                    message = context.getString(R.string.bedtime_reminder_body),
                    channelId = NotificationHelper.CHANNEL_ID_REMINDERS
                )
            }
        }
    }
}
