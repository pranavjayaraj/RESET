package com.reset.model.domain

import com.reset.model.domain.model.HomePreferences
import com.reset.model.domain.model.SessionStats
import kotlinx.coroutines.flow.Flow

/**
 * Persistence boundary for the AFK feature. Reads are exposed as cold [Flow]s so
 * the ViewModel can drive State reactively; writes are suspending.
 */
interface HomeRepository {

    val preferences: Flow<HomePreferences>

    val stats: Flow<SessionStats>

    suspend fun setDuration(minutes: Int)

    suspend fun setRemindersEnabled(enabled: Boolean)

    suspend fun setReminderEveryMin(minutes: Int)

    suspend fun setReminderStartHour(hour: Int)

    suspend fun setReminderEndHour(hour: Int)
}
