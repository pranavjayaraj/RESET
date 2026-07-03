package com.reset.repository.notification

import com.reset.model.domain.ReminderAction
import com.reset.model.domain.ReminderActionStore
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Activity-retained [ReminderActionStore]: shared by the host Activity and its ViewModels,
 * surviving rotation but dying with the activity instance — so a pending action can never
 * leak into a later app session. The action is plain state (not a queued event): a
 * cold-start dispatch simply waits as the current value until the Home ViewModel collects,
 * and it remains pending until explicitly consumed, so handling can't half-happen.
 */
@ActivityRetainedScoped
class ReminderActionStoreImpl @Inject constructor() : ReminderActionStore {

    private val _pending = MutableStateFlow<ReminderAction?>(null)
    override val pending: StateFlow<ReminderAction?> = _pending.asStateFlow()

    override fun dispatch(action: ReminderAction) {
        _pending.value = action
    }

    override fun consume(action: ReminderAction) {
        _pending.compareAndSet(action, null)
    }
}
