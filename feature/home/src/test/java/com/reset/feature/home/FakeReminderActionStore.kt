package com.reset.feature.home

import com.reset.model.domain.ReminderAction
import com.reset.model.domain.ReminderActionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** State-backed like the real store, so tests can dispatch before the ViewModel collects. */
class FakeReminderActionStore : ReminderActionStore {
    private val _pending = MutableStateFlow<ReminderAction?>(null)
    override val pending: StateFlow<ReminderAction?> = _pending.asStateFlow()

    val consumed = mutableListOf<ReminderAction>()

    override fun dispatch(action: ReminderAction) {
        _pending.value = action
    }

    override fun consume(action: ReminderAction) {
        consumed += action
        _pending.compareAndSet(action, null)
    }
}
