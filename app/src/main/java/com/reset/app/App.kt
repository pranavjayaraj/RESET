package com.reset.app

import android.app.Application
import com.reset.model.domain.ReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        // Arm the reminder chain for the persisted preferences (KEEP — never resets a
        // pending countdown). Covers first launch and app updates; WorkManager itself
        // persists the chain across reboots.
        appScope.launch { reminderScheduler.ensureScheduled() }
    }
}
