package com.reset.feature.home

import androidx.compose.runtime.Stable
import com.reset.model.domain.model.EyeFact
import com.reset.model.domain.model.SessionStats

/**
 * Immutable UI state for the AFK feature.
 *
 * [status] is the initial data-load lifecycle (loading / content / error) and [screen]
 * is the current destination within the flow. Actual screen rendering is driven by the
 * NavHost via navigation side effects; these fields let the ViewModel own load-state and
 * back-press logic. Build new state only with [getDefault] + `copy`.
 */
@Stable
data class AfkState(
    val status: AfkStatus = AfkStatus.Loading,
    val screen: AfkScreen = AfkScreen.Home,
    val fact: EyeFact = EyeFact(index = 0),
    val durationMin: Int = 5,
    val stats: SessionStats = SessionStats(),
    val remindersEnabled: Boolean = true,
    val showReminderBanner: Boolean = false,
    val leaving: Boolean = false,
) {
    companion object {
        fun getDefault() = AfkState()
    }
}

/** Initial data-load lifecycle for prefs + stats. */
sealed interface AfkStatus {
    data object Loading : AfkStatus
    data object Content : AfkStatus
    data class Error(val message: String?) : AfkStatus
}

/** Destination within the single-Activity AFK flow. */
sealed interface AfkScreen {
    data object Home : AfkScreen

    /** Built in a later milestone; rendered as a stub for now. */
    data object Session : AfkScreen

    /** Built in a later milestone; rendered as a stub for now. */
    data object Settings : AfkScreen
}
