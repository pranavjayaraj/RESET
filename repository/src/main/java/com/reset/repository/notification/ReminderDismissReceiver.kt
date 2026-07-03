package com.reset.repository.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

/** Dismisses the reminder when the user taps "Later" — the next nudge stays scheduled. */
class ReminderDismissReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != NotificationConstants.ACTION_DISMISS_REMINDER) return
        NotificationManagerCompat.from(context)
            .cancel(NotificationConstants.REMINDER_NOTIFICATION_ID)
    }
}
