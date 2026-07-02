package com.reset.feature.home

import com.reset.model.domain.ReminderAction
import com.reset.model.domain.ReminderActionStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/** Buffered like the real store, so tests can dispatch before the ViewModel collects. */
class FakeReminderActionStore : ReminderActionStore {
    private val _actions = Channel<ReminderAction>(Channel.BUFFERED)
    override val actions: Flow<ReminderAction> = _actions.receiveAsFlow()

    override fun dispatch(action: ReminderAction) {
        _actions.trySend(action)
    }
}
