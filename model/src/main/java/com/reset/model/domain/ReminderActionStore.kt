package com.reset.model.domain

import kotlinx.coroutines.flow.StateFlow

/** App-level actions a reminder notification can request. Pure data — no platform types. */
sealed interface ReminderAction {

    /** The notification's "Reset" button: begin a sit as soon as Home is ready. */
    data object StartReset : ReminderAction
}

/**
 * Seam between the notification entry point and the feature layer. Pull semantics, like an
 * Activity reading its intent: the host publishes the [pending] action as state, and it
 * stays readable until the handler calls [consume] after acting on it. Delivery therefore
 * never depends on a collector being live at dispatch time, collector churn can't lose an
 * action mid-handling, and a stale action can't replay once consumed.
 */
interface ReminderActionStore {

    /** The action awaiting handling, or null when there is none. */
    val pending: StateFlow<ReminderAction?>

    fun dispatch(action: ReminderAction)

    /** Acknowledge [action] as handled; a no-op if a different action is now pending. */
    fun consume(action: ReminderAction)
}
