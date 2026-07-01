package com.reset.repository.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.reset.model.domain.HomeRepository
import com.reset.model.domain.model.HomePreferences
import com.reset.model.domain.model.SessionStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** [HomeRepository] backed by Jetpack [DataStore] preferences. */
class HomeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : HomeRepository {

    override val preferences: Flow<HomePreferences> = dataStore.data.map { prefs ->
        HomePreferences(
            durationMin = prefs[KEY_DURATION] ?: HomePreferences.DEFAULT_DURATION_MIN,
            remindersEnabled = prefs[KEY_REMINDERS_ENABLED] ?: true,
            remindersEveryMin = prefs[KEY_REMINDER_EVERY] ?: HomePreferences.DEFAULT_REMINDER_EVERY_MIN,
            remindersStartHour = prefs[KEY_REMINDER_START] ?: HomePreferences.DEFAULT_REMINDER_START_HOUR,
            remindersEndHour = prefs[KEY_REMINDER_END] ?: HomePreferences.DEFAULT_REMINDER_END_HOUR,
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

    override suspend fun setReminderEveryMin(minutes: Int) {
        dataStore.edit { it[KEY_REMINDER_EVERY] = minutes }
    }

    override suspend fun setReminderStartHour(hour: Int) {
        dataStore.edit { it[KEY_REMINDER_START] = hour }
    }

    override suspend fun setReminderEndHour(hour: Int) {
        dataStore.edit { it[KEY_REMINDER_END] = hour }
    }

    private companion object {
        val KEY_DURATION = intPreferencesKey("duration_min")
        val KEY_REMINDERS_ENABLED = booleanPreferencesKey("reminders_enabled")
        val KEY_REMINDER_EVERY = intPreferencesKey("reminder_every_min")
        val KEY_REMINDER_START = intPreferencesKey("reminder_start_hour")
        val KEY_REMINDER_END = intPreferencesKey("reminder_end_hour")
        val KEY_SESSIONS = intPreferencesKey("sessions")
        val KEY_TOTAL_MIN = intPreferencesKey("total_min")
        val KEY_STREAK = intPreferencesKey("streak")
    }
}
