package com.reset.feature.home

import com.reset.model.domain.HomeRepository
import com.reset.model.domain.model.HomePreferences
import com.reset.model.domain.model.SessionStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

/** In-memory [HomeRepository] for ViewModel tests. */
class FakeHomeRepository(
    preferences: HomePreferences = HomePreferences(),
    stats: SessionStats = SessionStats(),
    private val failPreferences: Boolean = false,
) : HomeRepository {

    private val preferencesFlow = MutableStateFlow(preferences)
    private val statsFlow = MutableStateFlow(stats)

    var lastDuration: Int? = null
        private set
    var lastRemindersEnabled: Boolean? = null
        private set

    override val preferences: Flow<HomePreferences> =
        if (failPreferences) flow { throw IllegalStateException("boom") } else preferencesFlow

    override val stats: Flow<SessionStats> = statsFlow

    override suspend fun setDuration(minutes: Int) {
        lastDuration = minutes
        preferencesFlow.value = preferencesFlow.value.copy(durationMin = minutes)
    }

    override suspend fun setRemindersEnabled(enabled: Boolean) {
        lastRemindersEnabled = enabled
        preferencesFlow.value = preferencesFlow.value.copy(remindersEnabled = enabled)
    }

    override suspend fun setReminderEveryMin(minutes: Int) {
        preferencesFlow.value = preferencesFlow.value.copy(remindersEveryMin = minutes)
    }

    override suspend fun setReminderStartHour(hour: Int) {
        preferencesFlow.value = preferencesFlow.value.copy(remindersStartHour = hour)
    }

    override suspend fun setReminderEndHour(hour: Int) {
        preferencesFlow.value = preferencesFlow.value.copy(remindersEndHour = hour)
    }
}
