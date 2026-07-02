package com.reset.repository.notification

/**
 * Builds and posts the reset-reminder notification (channel creation included).
 * Interface + injected impl, mirroring the vibely `NotificationUtil` seam, so the
 * worker and tests never touch the platform `NotificationManager` directly.
 */
interface ReminderNotificationUtil {

    /** True when the runtime permission (API 33+) and user notification settings allow posting. */
    fun canPostNotifications(): Boolean

    /** Posts the reminder, creating the channel on first use. No-op when [canPostNotifications] is false. */
    fun showReminderNotification()

    /** Clears a shown reminder — action buttons don't auto-cancel, so the host calls this. */
    fun cancelReminderNotification()
}
