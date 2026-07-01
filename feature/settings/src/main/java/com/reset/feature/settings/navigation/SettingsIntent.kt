package com.reset.feature.settings.navigation

/** All user/UI intents for the Settings feature. */
sealed interface SettingsIntent {
    data object ToggleReminders : SettingsIntent
    data class SelectEveryMin(val minutes: Int) : SettingsIntent

    /** Nudge the reminder window boundaries by whole hours; the ViewModel clamps. */
    data class AdjustStartHour(val delta: Int) : SettingsIntent
    data class AdjustEndHour(val delta: Int) : SettingsIntent

    data object HandleBackPress : SettingsIntent
}
