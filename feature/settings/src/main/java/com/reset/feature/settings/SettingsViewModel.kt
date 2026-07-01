package com.reset.feature.settings

import androidx.lifecycle.SavedStateHandle
import com.reset.core.mvi.BaseViewModel
import com.reset.model.domain.HomeRepository
import com.reset.model.domain.model.HomePreferences
import com.reset.navigation.Navigator
import com.reset.feature.settings.navigation.SettingsIntent
import com.reset.feature.settings.navigation.SettingsSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: HomeRepository,
    private val navigator: Navigator,
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
    }

    private fun selectEveryMin(minutes: Int) = intent {
        reduce { state.copy(everyMin = minutes) }
        repository.setReminderEveryMin(minutes)
    }

    /** Start stays within [MIN_HOUR, endHour - 1] so the window is always non-empty. */
    private fun adjustStartHour(delta: Int) = intent {
        val next = (state.startHour + delta)
            .coerceIn(HomePreferences.MIN_HOUR, state.endHour - 1)
        if (next == state.startHour) return@intent
        reduce { state.copy(startHour = next) }
        repository.setReminderStartHour(next)
    }

    /** End stays within [startHour + 1, MAX_HOUR]. */
    private fun adjustEndHour(delta: Int) = intent {
        val next = (state.endHour + delta)
            .coerceIn(state.startHour + 1, HomePreferences.MAX_HOUR)
        if (next == state.endHour) return@intent
        reduce { state.copy(endHour = next) }
        repository.setReminderEndHour(next)
    }

    private fun close() = intent {
        navigator.pop()
    }
}
