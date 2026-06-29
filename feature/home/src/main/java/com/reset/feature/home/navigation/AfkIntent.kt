package com.reset.feature.home.navigation

/** All user/UI intents for the AFK feature. */
sealed interface AfkIntent {
    data object Load : AfkIntent
    data class SelectDuration(val minutes: Int) : AfkIntent
    data object TapReset : AfkIntent
    data object DismissReminderBanner : AfkIntent
    data object OpenSettings : AfkIntent
    data object HandleBackPress : AfkIntent
    data object Retry : AfkIntent
}
