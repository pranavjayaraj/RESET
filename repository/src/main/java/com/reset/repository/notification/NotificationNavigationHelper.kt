package com.reset.repository.notification

import android.content.Intent

/**
 * Seam between the notification layer and the app's `DeeplinkHandlerActivity`, mirroring
 * the sharechat/vibely `NotificationNavigationHelper`: this module can't see the app
 * module's activity classes, so `:app` binds the impl that builds the trampoline intent.
 */
interface NotificationNavigationHelper {

    /** Intent for the deeplink trampoline; callers set the action/extras before wrapping it in a PendingIntent. */
    fun getDeeplinkHandlerActivityIntent(): Intent
}
