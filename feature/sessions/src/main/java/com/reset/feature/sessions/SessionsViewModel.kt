package com.reset.feature.sessions

import androidx.lifecycle.SavedStateHandle
import com.reset.core.mvi.BaseViewModel
import com.reset.feature.sessions.navigation.SessionsIntent
import com.reset.feature.sessions.navigation.SessionsSideEffect
import com.reset.model.domain.HomeRepository
import com.reset.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

@HiltViewModel
class SessionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: HomeRepository,
    private val navigator: Navigator,
) : BaseViewModel<SessionsState, SessionsSideEffect>(savedStateHandle) {

    override fun initialState() = SessionsState.getDefault()

    override fun initData() {
        load()
    }

    fun handleSessionsIntent(intent: SessionsIntent) = when (intent) {
        SessionsIntent.Load -> load()
        SessionsIntent.Retry -> load()
        SessionsIntent.HandleBackPress -> close()
    }

    /** Streams the aggregate sit history; the flow stays hot so a sit finished on the
     *  Home tab is reflected here the moment the user switches back. */
    private fun load() = intent {
        reduce { state.copy(status = SessionsStatus.Loading) }
        repository.stats
            .catch { error ->
                reduce { state.copy(status = SessionsStatus.Error(error.message)) }
            }.collect { stats ->
                reduce { state.copy(status = SessionsStatus.Content, stats = stats) }
            }
    }

    /** Back on a non-start tab returns to Home (the start destination), per dashboard
     *  semantics — the host pops the stack that SwitchTab left one level deep. */
    private fun close() = intent {
        navigator.pop()
    }
}
