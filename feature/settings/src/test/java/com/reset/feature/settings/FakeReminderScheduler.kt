package com.reset.feature.settings

import com.reset.model.domain.ReminderScheduler

/** Recording [ReminderScheduler] for Settings ViewModel tests. */
class FakeReminderScheduler : ReminderScheduler {

    var preferencesChangedCount = 0
        private set
    var ensureScheduledCount = 0
        private set

    override suspend fun onPreferencesChanged() {
        preferencesChangedCount++
    }

    override suspend fun ensureScheduled() {
        ensureScheduledCount++
    }
}
