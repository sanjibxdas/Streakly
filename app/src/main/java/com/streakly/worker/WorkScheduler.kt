package com.streakly.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkScheduler {

    fun schedulePeriodicWork(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val healthConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val healthSyncRequest = PeriodicWorkRequestBuilder<HealthSyncWorker>(
            6, TimeUnit.HOURS,
            30, TimeUnit.MINUTES
        )
            .setConstraints(healthConstraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            HealthSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            healthSyncRequest
        )

        val notificationRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            12, TimeUnit.HOURS,
            1, TimeUnit.HOURS
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            NotificationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            notificationRequest
        )
    }
}
