package com.reset.feature.home

import androidx.compose.runtime.Stable
import com.reset.model.domain.model.EyeFact
import com.reset.model.domain.model.SessionStats

/**
 * Immutable UI state for the AFK feature.
 *
 * [status] is the initial data-load lifecycle (loading / content / error) and [screen]
 * is the current Home sub-screen. `HomeRoute` renders directly from these fields; cross-
 * feature navigation goes through the injected Navigator, not state. Build new state only
 * with [getDefault] + `copy`.
 */
@Stable
data class HomeState(
    val status: HomeStatus = HomeStatus.Loading,
    val screen: HomeStep = HomeStep.Home,
    val fact: EyeFact = EyeFact(index = 0),
    val durationMin: Int = 5,
    val stats: SessionStats = SessionStats(),
    val remindersEnabled: Boolean = true,
    val showReminderBanner: Boolean = false,
    val leaving: Boolean = false,
    /** Seconds left in the active meditation session; 0 when no session is running. */
    val remainingSeconds: Int = 0,
) {
    companion object {
        fun getDefault() = HomeState()
    }
}

/** Initial data-load lifecycle for prefs + stats. */
sealed interface HomeStatus {
    data object Loading : HomeStatus
    data object Content : HomeStatus
    data class Error(val message: String?) : HomeStatus
}

/** Which Home sub-screen is showing — an intra-feature transition, not app navigation. */
sealed interface HomeStep {
    data object Home : HomeStep

    /** The meditation session, with its own countdown screen. */
    data object Session : HomeStep
}
