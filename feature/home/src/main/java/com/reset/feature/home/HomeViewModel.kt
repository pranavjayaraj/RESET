package com.reset.feature.home

import androidx.lifecycle.SavedStateHandle
import com.reset.core.mvi.BaseViewModel
import com.reset.feature.home.HomeConstants.SESSION_TICK_MS
import com.reset.model.domain.HomeRepository
import com.reset.model.domain.EyeFactProvider
import com.reset.model.domain.ReminderAction
import com.reset.model.domain.ReminderActionStore
import com.reset.model.domain.model.ChimeKind
import com.reset.navigation.AppDestination
import com.reset.navigation.Navigator
import com.reset.feature.home.navigation.HomeIntent
import com.reset.feature.home.navigation.HomeSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: HomeRepository,
    private val eyeFactProvider: EyeFactProvider,
    private val navigator: Navigator,
    private val reminderActionStore: ReminderActionStore,
) : BaseViewModel<HomeState, HomeSideEffect>(savedStateHandle) {

    override fun initialState() = HomeState.getDefault()

    override fun initData() {
        load()
        observeReminderActions()
    }

    fun handleHomeIntent(intent: HomeIntent) = when (intent) {
        HomeIntent.Load -> load()
        HomeIntent.Retry -> retry()
        is HomeIntent.SelectDuration -> selectDuration(intent.minutes)
        HomeIntent.TapReset -> tapReset()
        HomeIntent.LeaveAnimationFinished -> beginSession()
        HomeIntent.FinishSession -> finishSession()
        HomeIntent.OpenSettings -> openSettings()
        HomeIntent.HandleBackPress -> onBackPress()
    }

    private fun load() = intent {
        reduce { state.copy(status = HomeStatus.Loading, fact = eyeFactProvider.random()) }
        // The preferences/stats flow stays hot for the whole session so the UI tracks
        // later changes (e.g. duration writes). Load state (Loading/Content/Error) is a
        // pure state transition — the host renders the right screen from it.
        combine(repository.preferences, repository.stats) { prefs, stats -> prefs to stats }
            .catch { error ->
                reduce { state.copy(status = HomeStatus.Error(error.message)) }
            }.collect { (prefs, stats) ->
                reduce {
                    state.copy(
                        status = HomeStatus.Content,
                        durationMin = prefs.durationMin,
                        remindersEnabled = prefs.remindersEnabled,
                        stats = stats,
                    )
                }
            }
    }

    private fun retry() = intent {
        load()
    }

    private fun selectDuration(minutes: Int) = intent {
        reduce { state.copy(durationMin = minutes) }
        repository.setDuration(minutes)
    }

    private fun tapReset() = intent {
        if (state.leaving || state.screen == HomeStep.Session) return@intent
        // Kick off the departure spin on Home. The transition to the session is driven
        // by the UI once the animation completes (HomeIntent.LeaveAnimationFinished).
        reduce { state.copy(leaving = true) }
        postSideEffect(HomeSideEffect.PlayChime(ChimeKind.Start))
    }

    /** Observes the pending reset request published by the host when the reminder
     *  notification's "Reset" action is tapped. Pull semantics: the action stays pending
     *  until [startResetFromReminder] consumes it after acting, so it survives collector
     *  churn and can't half-happen. */
    private fun observeReminderActions() = intent {
        reminderActionStore.pending.collect { action ->
            when (action) {
                ReminderAction.StartReset -> startResetFromReminder()
                null -> Unit
            }
        }
    }

    /**
     * The notification's "Reset" button: begin sitting right away, skipping the departure
     * animation (on a cold start Home may not even be composed yet). Waits for the load to
     * settle so the persisted duration is available. Every exit consumes the action — on
     * error it is deliberately dropped (the user lands on the error screen), and mid-session
     * it is a no-op; only then is it acknowledged so a stale request can never linger.
     */
    private fun startResetFromReminder() = intent {
        val settled = stateFlow().first {
            it.status == HomeStatus.Content || it.status is HomeStatus.Error
        }
        if (settled.status != HomeStatus.Content || state.screen == HomeStep.Session || state.leaving) {
            reminderActionStore.consume(ReminderAction.StartReset)
            return@intent
        }
        // The user may be on another destination (e.g. Settings); bring Home back on top
        // so the session is actually visible. No-op when Home is already showing.
        navigator.popTo(AppDestination.Home)
        postSideEffect(HomeSideEffect.PlayChime(ChimeKind.Start))
        enterSession()
        reminderActionStore.consume(ReminderAction.StartReset)
    }

    /** Advances to the meditation session (an intra-feature state change) once the leave
     *  animation has finished. */
    private fun beginSession() = intent {
        if (!state.leaving) return@intent
        enterSession()
    }

    private fun enterSession() = intent {
        reduce {
            state.copy(
                screen = HomeStep.Session,
                leaving = false,
                remainingSeconds = state.durationMin * 60,
            )
        }
        runSessionCountdown()
    }

    /**
     * Ticks the meditation countdown down to zero, then returns Home. Guarded by the
     * current screen so leaving the session (back / finish) stops the loop.
     */
    private fun runSessionCountdown() = intent {
        while (state.screen == HomeStep.Session && state.remainingSeconds > 0) {
            delay(SESSION_TICK_MS)
            reduce {
                if (state.screen == HomeStep.Session) {
                    state.copy(remainingSeconds = (state.remainingSeconds - 1).coerceAtLeast(0))
                } else {
                    state
                }
            }
        }
        if (state.screen == HomeStep.Session && state.remainingSeconds == 0) {
            postSideEffect(HomeSideEffect.PlayChime(ChimeKind.End))
            finishSession()
        }
    }

    private fun finishSession() = intent {
        if (state.screen != HomeStep.Session) return@intent
        reduce { state.copy(screen = HomeStep.Home, remainingSeconds = 0, leaving = false) }
    }

    private fun openSettings() = intent {
        navigator.navigate(AppDestination.Settings)
    }

    private fun onBackPress() = intent {
        when (state.screen) {
            HomeStep.Home -> navigator.exit()
            HomeStep.Session -> reduce { state.copy(screen = HomeStep.Home, remainingSeconds = 0, leaving = false) }
        }
    }
}
