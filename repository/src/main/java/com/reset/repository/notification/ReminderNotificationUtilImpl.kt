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
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReminderNotificationUtilImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ReminderNotificationUtil {

    override fun canPostNotifications(): Boolean {
        val permissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        return permissionGranted && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    override fun showReminderNotification() {
        if (!canPostNotifications()) return
        createReminderChannel()

        val body = context.getString(R.string.reminder_notification_text)
        val openApp = launchAppIntent()
        val notification = NotificationCompat.Builder(context, NotificationConstants.REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_reset_reminder)
            .setColor(ContextCompat.getColor(context, R.color.reminder_accent))
            .setContentTitle(context.getString(R.string.reminder_notification_title))
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

    override fun cancelReminderNotification() {
        NotificationManagerCompat.from(context)
            .cancel(NotificationConstants.REMINDER_NOTIFICATION_ID)
    }

    /** Tapping the reminder body opens the app's single Activity via its launch intent. */
    private fun launchAppIntent(): PendingIntent? {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: return null
        return PendingIntent.getActivity(
            context,
            NotificationConstants.REMINDER_NOTIFICATION_ID,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /**
     * "Reset" launches the host Activity directly (no trampoline — banned on API 31+)
     * with [NotificationConstants.ACTION_START_RESET]; the host translates it into a
     * `ReminderAction.StartReset` dispatch. SINGLE_TOP delivers to `onNewIntent` when
     * the app is already in the foreground.
     */
    private fun startResetIntent(): PendingIntent? {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: return null
        launchIntent.action = NotificationConstants.ACTION_START_RESET
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return PendingIntent.getActivity(
            context,
            NotificationConstants.REMINDER_RESET_REQUEST_CODE,
            launchIntent,
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
