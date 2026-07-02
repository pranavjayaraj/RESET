package com.reset.repository.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.reset.model.domain.HomeRepository
import com.reset.model.domain.ReminderTimeCalculator
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * Self-chaining one-shot worker behind the reset reminders, following the vibely
 * `DailyNotificationWork` format: companion scheduling helpers over unique work,
 * a Hilt [EntryPoint] for dependencies (workers are framework-instantiated, so no
 * constructor injection), and each run enqueueing the next occurrence. Preferences
 * are read fresh at fire time, so a stale chain can never show stale settings.
 */
class ReminderNotificationWork(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    companion object {

        /** Enqueues the next reminder [delayMillis] from now as unique work under the reminder tag. */
        fun schedule(context: Context, delayMillis: Long, policy: ExistingWorkPolicy) {
            val request = OneTimeWorkRequestBuilder<ReminderNotificationWork>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(
                    Data.Builder()
                        .putLong(
                            NotificationConstants.KEY_SCHEDULE_TIME_MS,
                            System.currentTimeMillis() + delayMillis,
                        )
                        .build(),
                )
                .addTag(NotificationConstants.REMINDER_WORK_TAG)
                .build()

            WorkManager.getInstance(context)
                .beginUniqueWork(NotificationConstants.REMINDER_WORK_TAG, policy, request)
                .enqueue()
        }

        /** Stops the reminder chain (reminders toggled off). */
        fun cancel(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(NotificationConstants.REMINDER_WORK_TAG)
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ReminderNotificationWorkEntryPoint {
        fun homeRepository(): HomeRepository
        fun reminderNotificationUtil(): ReminderNotificationUtil
    }

    private lateinit var hiltEntryPoint: ReminderNotificationWorkEntryPoint

    private val homeRepository: HomeRepository by lazy { hiltEntryPoint.homeRepository() }

    private val notificationUtil: ReminderNotificationUtil by lazy {
        hiltEntryPoint.reminderNotificationUtil()
    }

    override suspend fun doWork(): Result {
        hiltEntryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            ReminderNotificationWorkEntryPoint::class.java,
        )

        val prefs = homeRepository.preferences.first()
        // Toggled off after this run was enqueued — end the chain without posting.
        if (!prefs.remindersEnabled) return Result.success()

        val now = System.currentTimeMillis()
        // WorkManager can fire late; only post if we are still inside the user's window.
        if (ReminderTimeCalculator.isWithinWindow(now, prefs.remindersStartHour, prefs.remindersEndHour)) {
            notificationUtil.showReminderNotification()
        }

        val nextDelay = ReminderTimeCalculator.nextTriggerMillis(
            nowMillis = now,
            everyMin = prefs.remindersEveryMin,
            startHour = prefs.remindersStartHour,
            endHour = prefs.remindersEndHour,
        ) - now
        // REPLACE: this (still-running) work occupies the unique name, so KEEP would drop the chain.
        schedule(applicationContext, nextDelay, ExistingWorkPolicy.REPLACE)

        return Result.success()
    }
}
