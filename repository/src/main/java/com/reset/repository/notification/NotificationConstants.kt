package com.reset.repository.notification

/** All notification channel/work identifiers — no magic strings in workers or utils. */
object NotificationConstants {

    /** Channel the reset reminders post on. */
    const val REMINDER_CHANNEL_ID = "reset_reminders"

    /** Unique-work name + tag for the self-chaining reminder worker. */
    const val REMINDER_WORK_TAG = "reminder_notification"

    /** Fixed id — each reminder replaces the previous one instead of stacking. */
    const val REMINDER_NOTIFICATION_ID = 1001

    /** Request code for the "Later" dismiss action's broadcast PendingIntent. */
    const val REMINDER_DISMISS_REQUEST_CODE = 1002

    /** Request code for the "Reset" action's activity PendingIntent. */
    const val REMINDER_RESET_REQUEST_CODE = 1003

    /** Broadcast action the "Later" button fires to dismiss the reminder. */
    const val ACTION_DISMISS_REMINDER = "com.reset.action.DISMISS_REMINDER"

    /** Intent action the "Reset" button launches the host Activity with to begin a sit. */
    const val ACTION_START_RESET = "com.reset.action.START_RESET"

    /** Worker input: the epoch millis this run was scheduled to fire at. */
    const val KEY_SCHEDULE_TIME_MS = "schedule_time_ms"

    /** Set by the deeplink trampoline so the host knows the launch came through it. */
    const val EXTRA_FROM_DEEPLINK_ACTIVITY = "from_deeplink_activity"
}
