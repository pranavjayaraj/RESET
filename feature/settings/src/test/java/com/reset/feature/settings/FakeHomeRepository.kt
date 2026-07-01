package com.reset.feature.settings

import com.reset.model.domain.HomeRepository
import com.reset.model.domain.model.HomePreferences
import com.reset.model.domain.model.SessionStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory [HomeRepository] for Settings ViewModel tests. */
class FakeHomeRepository(
    preferences: HomePreferences = HomePreferences(),
) : HomeRepository {

    private val preferencesFlow = MutableStateFlow(preferences)

    var lastRemindersEnabled: Boolean? = null
        private set
    var lastEveryMin: Int? = null
        private set
    var lastStartHour: Int? = null
        private set
    var lastEndHour: Int? = null
        private set

    override val preferences: Flow<HomePreferences> = preferencesFlow

    override val stats: Flow<SessionStats> = MutableStateFlow(SessionStats())

    override suspend fun setDuration(minutes: Int) {
        preferencesFlow.value = preferencesFlow.value.copy(durationMin = minutes)
    }

    override suspend fun setRemindersEnabled(enabled: Boolean) {
        lastRemindersEnabled = enabled
        preferencesFlow.value = preferencesFlow.value.copy(remindersEnabled = enabled)
    }

    override suspend fun setReminderEveryMin(minutes: Int) {
        lastEveryMin = minutes
        preferencesFlow.value = preferencesFlow.value.copy(remindersEveryMin = minutes)
    }

    override suspend fun setReminderStartHour(hour: Int) {
        lastStartHour = hour
        preferencesFlow.value = preferencesFlow.value.copy(remindersStartHour = hour)
    }

    override suspend fun setReminderEndHour(hour: Int) {
        lastEndHour = hour
        preferencesFlow.value = preferencesFlow.value.copy(remindersEndHour = hour)
    }
}
