package com.reset.repository.notification

import com.reset.model.domain.ReminderAction
import com.reset.model.domain.ReminderActionStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App-scoped [ReminderActionStore]. Actions go through a buffered [Channel] so the host
 * can dispatch from a cold-start intent before the Home ViewModel begins collecting.
 */
@Singleton
class ReminderActionStoreImpl @Inject constructor() : ReminderActionStore {

    private val _actions = Channel<ReminderAction>(Channel.BUFFERED)
    override val actions: Flow<ReminderAction> = _actions.receiveAsFlow()

    override fun dispatch(action: ReminderAction) {
        _actions.trySend(action)
    }
}
