package com.reset.feature.settings.navigation

/**
 * Feature-local one-shot effects for Settings. Navigation (including closing) is NOT
 * here — it goes through the injected [com.reset.navigation.Navigator].
 */
sealed interface SettingsSideEffect {

    /**
     * Reminders were just enabled: the Route should run the POST_NOTIFICATIONS runtime
     * permission flow (API 33+) and report back via
     * [SettingsIntent.UpdateNotificationPermission].
     */
    data object RequestNotificationPermission : SettingsSideEffect
}
