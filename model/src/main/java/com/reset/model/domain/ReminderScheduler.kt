package com.reset.model.domain

/**
 * Domain seam for the reset-reminder notification schedule. The Settings ViewModel calls
 * [onPreferencesChanged] after persisting a reminder preference; the app host calls
 * [ensureScheduled] at startup so a schedule always exists for the persisted preferences.
 * The data-module implementation maps these onto WorkManager unique work.
 */
interface ReminderScheduler {

    /** Re-arms (or cancels) the reminder chain to match freshly persisted preferences. */
    suspend fun onPreferencesChanged()

    /** Arms the reminder chain only if none is pending — safe to call on every app start. */
    suspend fun ensureScheduled()
}
