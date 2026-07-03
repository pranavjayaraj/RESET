package com.reset.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.reset.repository.notification.NotificationConstants

/**
 * Trampoline for every notification tap (and future external deeplinks), mirroring the
 * sharechat/vibely `DeeplinkHandlerActivity`: it never draws UI — it retargets the incoming
 * intent at [MainActivity], preserving the action/extras for `handleReminderAction`, and
 * removes itself without an animation. Keeping this hop out of [MainActivity] gives
 * notifications a single stable entry point that doesn't depend on the host's launch mode
 * or current task state.
 *
 * Unlike the reference's `CLEAR_TASK`, the host is launched `CLEAR_TOP or SINGLE_TOP`: a
 * running [MainActivity] receives the intent via `onNewIntent` instead of being destroyed,
 * so a tap never kills an in-progress session; a cold tap still creates the task normally.
 */
class DeeplinkHandlerActivity : Activity() {

    companion object {
        fun getDeeplinkHandlerActivityIntent(context: Context): Intent =
            Intent(context, DeeplinkHandlerActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Relaunching the task from recents re-delivers the original notification intent;
        // strip the action so a stale tap isn't replayed as a fresh one.
        if (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY != 0) {
            intent.action = null
        }
        intent.setClass(this, MainActivity::class.java)
            .setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP,
            )
        intent.putExtra(NotificationConstants.EXTRA_FROM_DEEPLINK_ACTIVITY, true)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}
