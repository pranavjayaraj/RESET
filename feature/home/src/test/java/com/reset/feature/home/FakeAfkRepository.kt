package com.reset.feature.home

import com.reset.model.domain.AfkRepository
import com.reset.model.domain.model.AfkPreferences
import com.reset.model.domain.model.SessionStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

/** In-memory [AfkRepository] for ViewModel tests. */
class FakeAfkRepository(
    preferences: AfkPreferences = AfkPreferences(),
    stats: SessionStats = SessionStats(),
    private val failPreferences: Boolean = false,
) : AfkRepository {

    private val preferencesFlow = MutableStateFlow(preferences)
    private val statsFlow = MutableStateFlow(stats)

    var lastDuration: Int? = null
        private set
    var lastRemindersEnabled: Boolean? = null
        private set

    override val preferences: Flow<AfkPreferences> =
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
}
