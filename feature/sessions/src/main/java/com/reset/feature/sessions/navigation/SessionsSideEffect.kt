package com.reset.feature.sessions.navigation

/**
 * Feature-local one-shot effects for Sessions. Navigation (including closing) is NOT
 * here — it goes through the injected [com.reset.navigation.Navigator]. The feature has
 * no one-shot effects yet; cases (toast, chime, ...) are added here as they appear.
 */
sealed interface SessionsSideEffect
