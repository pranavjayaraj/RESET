package com.reset.feature.settings

import androidx.compose.runtime.Stable
import com.reset.model.domain.model.HomePreferences

/**
 * Immutable UI state for the Settings feature — the reset-reminder configuration.
 * Seeded from persisted [HomePreferences]; build new state only with [getDefault] + `copy`.
 */
@Stable
data class SettingsState(
    val loaded: Boolean = false,
    val remindersEnabled: Boolean = true,
    val everyMin: Int = HomePreferences.DEFAULT_REMINDER_EVERY_MIN,
    val startHour: Int = HomePreferences.DEFAULT_REMINDER_START_HOUR,
    val endHour: Int = HomePreferences.DEFAULT_REMINDER_END_HOUR,
) {
    companion object {
        fun getDefault() = SettingsState()
    }
}
