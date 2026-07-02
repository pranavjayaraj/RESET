package com.reset.model.domain

import kotlinx.coroutines.flow.Flow

/** App-level actions a reminder notification can request. Pure data — no platform types. */
sealed interface ReminderAction {

    /** The notification's "Reset" button: begin a sit as soon as Home is ready. */
    data object StartReset : ReminderAction
}

/**
 * Seam between the notification entry point and the feature layer, mirroring the
 * `Navigator` pattern: the host Activity translates a notification action intent into a
 * [ReminderAction], and the owning ViewModel collects [actions] and reacts. Buffered so
 * an action dispatched during a cold launch waits for the collector.
 */
interface ReminderActionStore {

    val actions: Flow<ReminderAction>

    fun dispatch(action: ReminderAction)
}
