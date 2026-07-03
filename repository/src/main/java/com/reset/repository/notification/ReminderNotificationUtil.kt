package com.reset.repository.notification

import com.reset.model.domain.ReminderNotificationType

/**
 * Builds and posts the reset-reminder notification (channel creation included).
 * Interface + injected impl, mirroring the vibely `NotificationUtil` seam, so the
 * worker and tests never touch the platform `NotificationManager` directly.
 */
interface ReminderNotificationUtil {

    /** True when the runtime permission (API 33+) and user notification settings allow posting. */
    fun canPostNotifications(): Boolean

    /** Posts the reminder [type], creating the channel on first use. No-op when [canPostNotifications] is false. */
    fun showReminderNotification(type: ReminderNotificationType)

    /** Clears a shown reminder — action buttons don't auto-cancel, so the host calls this. */
    fun cancelReminderNotification()
}
