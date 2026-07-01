package com.reset.feature.home.navigation

/** All user/UI intents for the AFK feature. */
sealed interface HomeIntent {
    data object Load : HomeIntent
    data class SelectDuration(val minutes: Int) : HomeIntent
    data object TapReset : HomeIntent
    data object LeaveAnimationFinished : HomeIntent
    data object FinishSession : HomeIntent
    data object DismissReminderBanner : HomeIntent
    data object OpenSettings : HomeIntent
    data object HandleBackPress : HomeIntent
    data object Retry : HomeIntent
}
