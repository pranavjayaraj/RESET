package com.reset.repository.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.reset.data.R
import com.reset.model.domain.ReminderNotificationType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReminderNotificationUtilImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val navigationHelper: NotificationNavigationHelper,
) : ReminderNotificationUtil {

    override fun canPostNotifications(): Boolean {
        val permissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        return permissionGranted && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    override fun showReminderNotification(type: ReminderNotificationType) {
        if (!canPostNotifications()) return
        createReminderChannel()

        val title = titleFor(type)
        val body = bodyFor(type)
        val openApp = launchAppIntent()
        val notification = NotificationCompat.Builder(context, NotificationConstants.REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_reset_reminder)
            .setColor(ContextCompat.getColor(context, R.color.reminder_accent))
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(openApp)
            .addAction(0, context.getString(R.string.reminder_action_later), dismissIntent())
            .addAction(0, context.getString(R.string.reminder_action_reset), startResetIntent())
            .build()

        @Suppress("MissingPermission") // guarded by canPostNotifications above
        NotificationManagerCompat.from(context)
            .notify(NotificationConstants.REMINDER_NOTIFICATION_ID, notification)
    }

    private fun titleFor(type: ReminderNotificationType): String = when (type) {
        ReminderNotificationType.ResetNudge ->
            context.getString(R.string.reminder_notification_title)
        is ReminderNotificationType.StreakGuard ->
            context.getString(R.string.reminder_streak_title)
        is ReminderNotificationType.EyeFactNudge ->
            context.getString(R.string.reminder_fact_title)
    }

    private fun bodyFor(type: ReminderNotificationType): String = when (type) {
        ReminderNotificationType.ResetNudge ->
            context.getString(R.string.reminder_notification_text)
        is ReminderNotificationType.StreakGuard ->
            context.resources.getQuantityString(
                R.plurals.reminder_streak_text, type.streakDays, type.streakDays,
            )
        is ReminderNotificationType.EyeFactNudge -> {
            val facts = context.resources.getStringArray(R.array.reminder_eye_fact_texts)
            facts[type.fact.index.coerceIn(0, facts.lastIndex)]
        }
    }

    override fun cancelReminderNotification() {
        NotificationManagerCompat.from(context)
            .cancel(NotificationConstants.REMINDER_NOTIFICATION_ID)
    }

    /** Tapping the reminder body goes through the deeplink trampoline into the host Activity. */
    private fun launchAppIntent(): PendingIntent {
        return PendingIntent.getActivity(
            context,
            NotificationConstants.REMINDER_NOTIFICATION_ID,
            navigationHelper.getDeeplinkHandlerActivityIntent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /**
     * "Reset" also goes through the trampoline, carrying
     * [NotificationConstants.ACTION_START_RESET]; the trampoline retargets the intent at
     * the host (delivered via `onNewIntent` when the app is already running, so an
     * in-progress session survives), and the host translates the action into a
     * `ReminderAction.StartReset` dispatch. (Activity trampolines are allowed on API 31+ —
     * only receiver/service trampolines are banned.)
     */
    private fun startResetIntent(): PendingIntent {
        val intent = navigationHelper.getDeeplinkHandlerActivityIntent()
            .setAction(NotificationConstants.ACTION_START_RESET)
        return PendingIntent.getActivity(
            context,
            NotificationConstants.REMINDER_RESET_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /** "Later" clears the reminder without opening the app; the chain keeps running. */
    private fun dismissIntent(): PendingIntent {
        val intent = Intent(context, ReminderDismissReceiver::class.java)
            .setAction(NotificationConstants.ACTION_DISMISS_REMINDER)
        return PendingIntent.getBroadcast(
            context,
            NotificationConstants.REMINDER_DISMISS_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createReminderChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            NotificationConstants.REMINDER_CHANNEL_ID,
            context.getString(R.string.reminder_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.reminder_channel_description)
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}
