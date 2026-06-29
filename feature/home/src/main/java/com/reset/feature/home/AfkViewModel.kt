package com.reset.feature.home

import androidx.lifecycle.SavedStateHandle
import com.reset.core.mvi.BaseViewModel
import com.reset.feature.home.AfkConstants.LEAVE_ANIMATION_MS
import com.reset.feature.home.AfkConstants.REMINDER_BANNER_DELAY_MS
import com.reset.model.domain.AfkRepository
import com.reset.model.domain.EyeFactProvider
import com.reset.model.domain.model.ChimeKind
import com.reset.feature.home.navigation.AfkIntent
import com.reset.feature.home.navigation.AfkSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

@HiltViewModel
class AfkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AfkRepository,
    private val eyeFactProvider: EyeFactProvider,
) : BaseViewModel<AfkState, AfkSideEffect>(savedStateHandle) {

    override fun initialState() = AfkState.getDefault()

    override fun initData() {
        load()
    }

    fun handleAfkIntent(intent: AfkIntent) = when (intent) {
        AfkIntent.Load -> load()
        AfkIntent.Retry -> retry()
        is AfkIntent.SelectDuration -> selectDuration(intent.minutes)
        AfkIntent.TapReset -> tapReset()
        AfkIntent.DismissReminderBanner -> dismissBanner()
        AfkIntent.OpenSettings -> openSettings()
        AfkIntent.HandleBackPress -> onBackPress()
    }

    private fun load() = intent {
        reduce { state.copy(status = AfkStatus.Loading, fact = eyeFactProvider.random()) }
        scheduleReminderBanner()
        // The preferences/stats flow stays hot for the whole session so the UI tracks
        // later changes (e.g. duration writes). Navigation, however, is a one-shot:
        // re-posting ShowHomeScreen on every emission would re-navigate the NavHost,
        // tearing down HomeScreen and restarting its infinite animations (the "blink").
        combine(repository.preferences, repository.stats) { prefs, stats -> prefs to stats }
            .catch { error ->
                reduce { state.copy(status = AfkStatus.Error(error.message)) }
                postSideEffect(AfkSideEffect.ShowErrorScreen)
            }
            .collect { (prefs, stats) ->
                reduce {
                    state.copy(
                        status = AfkStatus.Content,
                        durationMin = prefs.durationMin,
                        remindersEnabled = prefs.remindersEnabled,
                        stats = stats,
                    )
                }                
            }
    }

    private fun retry() = intent {
        postSideEffect(AfkSideEffect.ShowLoadingScreen)
        load()
    }

    private fun scheduleReminderBanner() = intent {
        delay(REMINDER_BANNER_DELAY_MS)
        reduce {
            if (state.remindersEnabled && state.screen == AfkScreen.Home && !state.leaving) {
                state.copy(showReminderBanner = true)
            } else {
                state
            }
        }
    }

    private fun selectDuration(minutes: Int) = intent {
        reduce { state.copy(durationMin = minutes) }
        repository.setDuration(minutes)
    }

    private fun tapReset() = intent {
        if (state.leaving) return@intent
        reduce { state.copy(leaving = true, showReminderBanner = false) }
        postSideEffect(AfkSideEffect.PlayChime(ChimeKind.Start))
        delay(LEAVE_ANIMATION_MS)
        // Session screen is stubbed in this milestone; the flow still advances.
        reduce { state.copy(screen = AfkScreen.Session, leaving = false) }
        postSideEffect(AfkSideEffect.ShowSessionScreen)
    }

    private fun dismissBanner() = intent {
        reduce { state.copy(showReminderBanner = false) }
    }

    private fun openSettings() = intent {
        reduce { state.copy(screen = AfkScreen.Settings, showReminderBanner = false) }
        postSideEffect(AfkSideEffect.ShowSettingsScreen)
    }

    private fun onBackPress() = intent {
        if (state.screen == AfkScreen.Home) {
            postSideEffect(AfkSideEffect.CloseActivity)
        } else {
            reduce { state.copy(screen = AfkScreen.Home) }
            postSideEffect(AfkSideEffect.NavigateHome)
        }
    }
}
