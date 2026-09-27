package com.streakly.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.streakly.R
import com.streakly.domain.repository.HabitRepository
import com.streakly.domain.repository.SettingsRepository
import com.streakly.domain.repository.WaterRepository
import com.streakly.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val waterRepository: WaterRepository,
    private val habitRepository: HabitRepository,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val settings = settingsRepository.notificationSettings.firstOrNull()
            if (settings == null) return Result.success()

            // Check hydration
            if (settings.hydrationReminders) {
                val water = waterRepository.getTodayWater().firstOrNull()
                if (water != null && water.glassesCount < water.targetGlasses) {
                    NotificationHelper.showNotification(
                        context = appContext,
                        id = NotificationHelper.NOTIF_ID_WATER,
                        title = appContext.getString(R.string.water_reminder_title),
                        message = appContext.getString(R.string.water_reminder_body),
                        channelId = NotificationHelper.CHANNEL_ID_REMINDERS
                    )
                }
            }

            // Check habits streak at risk
            if (settings.streakWarnings) {
                val habits = habitRepository.getHabits().firstOrNull() ?: emptyList()
                val incompleteCount = habits.count { !it.isCompletedToday }
                if (incompleteCount > 0) {
                    NotificationHelper.showNotification(
                        context = appContext,
                        id = NotificationHelper.NOTIF_ID_STREAK,
                        title = appContext.getString(R.string.streak_at_risk_title),
                        message = appContext.getString(R.string.streak_at_risk_body),
                        channelId = NotificationHelper.CHANNEL_ID_REMINDERS
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "NotificationWorker"
    }
}
