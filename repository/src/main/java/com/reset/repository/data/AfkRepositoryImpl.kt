package com.reset.repository.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.reset.model.domain.AfkRepository
import com.reset.model.domain.model.AfkPreferences
import com.reset.model.domain.model.SessionStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** [AfkRepository] backed by Jetpack [DataStore] preferences. */
class AfkRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AfkRepository {

    override val preferences: Flow<AfkPreferences> = dataStore.data.map { prefs ->
        AfkPreferences(
            durationMin = prefs[KEY_DURATION] ?: AfkPreferences.DEFAULT_DURATION_MIN,
            remindersEnabled = prefs[KEY_REMINDERS_ENABLED] ?: true,
        )
    }

    override val stats: Flow<SessionStats> = dataStore.data.map { prefs ->
        SessionStats(
            sessions = prefs[KEY_SESSIONS] ?: 0,
            totalMin = prefs[KEY_TOTAL_MIN] ?: 0,
            streak = prefs[KEY_STREAK] ?: 0,
        )
    }

    override suspend fun setDuration(minutes: Int) {
        dataStore.edit { it[KEY_DURATION] = minutes }
    }

    override suspend fun setRemindersEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_REMINDERS_ENABLED] = enabled }
    }

    private companion object {
        val KEY_DURATION = intPreferencesKey("duration_min")
        val KEY_REMINDERS_ENABLED = booleanPreferencesKey("reminders_enabled")
        val KEY_SESSIONS = intPreferencesKey("sessions")
        val KEY_TOTAL_MIN = intPreferencesKey("total_min")
        val KEY_STREAK = intPreferencesKey("streak")
    }
}
