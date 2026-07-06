package com.reset.feature.sessions

import com.reset.model.domain.HomeRepository
import com.reset.model.domain.model.HomePreferences
import com.reset.model.domain.model.SessionStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

/** In-memory [HomeRepository] for Sessions ViewModel tests. */
class FakeHomeRepository(
    stats: SessionStats = SessionStats(),
    private val statsError: Throwable? = null,
) : HomeRepository {

    val statsFlow = MutableStateFlow(stats)

    override val preferences: Flow<HomePreferences> = MutableStateFlow(HomePreferences())

    override val stats: Flow<SessionStats> = flow {
        statsError?.let { throw it }
        statsFlow.collect { emit(it) }
    }

    override suspend fun setDuration(minutes: Int) = Unit

    override suspend fun setRemindersEnabled(enabled: Boolean) = Unit

    override suspend fun setReminderEveryMin(minutes: Int) = Unit

    override suspend fun setReminderStartHour(hour: Int) = Unit

    override suspend fun setReminderEndHour(hour: Int) = Unit
}
