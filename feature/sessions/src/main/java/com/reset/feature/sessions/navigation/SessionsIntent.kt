package com.reset.feature.sessions.navigation

/** All user/UI intents for the Sessions feature. */
sealed interface SessionsIntent {
    data object Load : SessionsIntent
    data object Retry : SessionsIntent
    data object HandleBackPress : SessionsIntent
}
