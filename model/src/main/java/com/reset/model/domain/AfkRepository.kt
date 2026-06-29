package com.reset.model.domain

import com.reset.model.domain.model.AfkPreferences
import com.reset.model.domain.model.SessionStats
import kotlinx.coroutines.flow.Flow

/**
 * Persistence boundary for the AFK feature. Reads are exposed as cold [Flow]s so
 * the ViewModel can drive State reactively; writes are suspending.
 */
interface AfkRepository {

    val preferences: Flow<AfkPreferences>

    val stats: Flow<SessionStats>

    suspend fun setDuration(minutes: Int)

    suspend fun setRemindersEnabled(enabled: Boolean)
}
