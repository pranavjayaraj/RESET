package com.reset.feature.settings

import androidx.lifecycle.SavedStateHandle
import com.reset.core.mvi.BaseViewModel
import com.reset.model.domain.HomeRepository
import com.reset.model.domain.ReminderScheduler
import com.reset.model.domain.model.HomePreferences
import com.reset.navigation.Navigator
import com.reset.feature.settings.navigation.SettingsIntent
import com.reset.feature.settings.navigation.SettingsSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: HomeRepository,
    private val navigator: Navigator,
    private val reminderScheduler: ReminderScheduler,
) : BaseViewModel<SettingsState, SettingsSideEffect>(savedStateHandle) {

    override fun initialState() = SettingsState.getDefault()

    override fun initData() {
        observePreferences()
    }

    fun handleSettingsIntent(intent: SettingsIntent) = when (intent) {
        SettingsIntent.ToggleReminders -> toggleReminders()
        is SettingsIntent.SelectEveryMin -> selectEveryMin(intent.minutes)
        is SettingsIntent.AdjustStartHour -> adjustStartHour(intent.delta)
        is SettingsIntent.AdjustEndHour -> adjustEndHour(intent.delta)
        is SettingsIntent.UpdateNotificationPermission -> onNotificationPermissionResult(intent.granted)
        SettingsIntent.HandleBackPress -> close()
    }

    /** Reflects persisted preferences into state and tracks later external writes. */
    private fun observePreferences() = intent {
        repository.preferences.collect { prefs ->
            reduce {
                state.copy(
                    loaded = true,
                    remindersEnabled = prefs.remindersEnabled,
                    everyMin = prefs.remindersEveryMin,
                    startHour = prefs.remindersStartHour,
                    endHour = prefs.remindersEndHour,
                )
            }
        }
    }

    private fun toggleReminders() = intent {
        val next = !state.remindersEnabled
        reduce { state.copy(remindersEnabled = next) }
        repository.setRemindersEnabled(next)
        reminderScheduler.onPreferencesChanged()
        if (next) postSideEffect(SettingsSideEffect.RequestNotificationPermission)
    }

    private fun selectEveryMin(minutes: Int) = intent {
        reduce { state.copy(everyMin = minutes) }
        repository.setReminderEveryMin(minutes)
        reminderScheduler.onPreferencesChanged()
    }

    /** Start stays within [MIN_HOUR, endHour - 1] so the window is always non-empty. */
    private fun adjustStartHour(delta: Int) = intent {
        val next = (state.startHour + delta)
            .coerceIn(HomePreferences.MIN_HOUR, state.endHour - 1)
        if (next == state.startHour) return@intent
        reduce { state.copy(startHour = next) }
        repository.setReminderStartHour(next)
        reminderScheduler.onPreferencesChanged()
    }

    /** End stays within [startHour + 1, MAX_HOUR]. */
    private fun adjustEndHour(delta: Int) = intent {
        val next = (state.endHour + delta)
            .coerceIn(state.startHour + 1, HomePreferences.MAX_HOUR)
        if (next == state.endHour) return@intent
        reduce { state.copy(endHour = next) }
        repository.setReminderEndHour(next)
        reminderScheduler.onPreferencesChanged()
    }

    /**
     * Permission result from the Route. Grant → make sure a chain is armed (KEEP, so an
     * in-flight countdown survives). Denial needs no action: the schedule stays and the
     * worker's own permission check keeps it silent until the user grants access.
     */
    private fun onNotificationPermissionResult(granted: Boolean) = intent {
        if (granted) reminderScheduler.ensureScheduled()
    }

    private fun close() = intent {
        navigator.pop()
    }
}
