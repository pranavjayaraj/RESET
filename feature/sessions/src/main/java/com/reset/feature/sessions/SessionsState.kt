package com.reset.feature.sessions

import androidx.compose.runtime.Stable
import com.reset.model.domain.model.SessionStats

/**
 * Immutable UI state for the Sessions feature — the sit-history dashboard tab.
 *
 * [status] is the data-load lifecycle (loading / content / error); `SessionsRoute` renders
 * the right surface from it. Cross-feature navigation goes through the injected Navigator,
 * not state. Build new state only with [getDefault] + `copy`.
 */
@Stable
data class SessionsState(
    val status: SessionsStatus = SessionsStatus.Loading,
    val stats: SessionStats = SessionStats(),
) {
    companion object {
        fun getDefault() = SessionsState()
    }
}

/** Data-load lifecycle for the sit history. */
sealed interface SessionsStatus {
    data object Loading : SessionsStatus
    data object Content : SessionsStatus
    data class Error(val message: String?) : SessionsStatus
}
