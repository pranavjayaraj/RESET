package com.reset.core.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

/**
 * Base class for every feature ViewModel, mirroring the `in.mohalla.mvi.BaseViewModel`
 * contract from the sharechat reference but built on the Orbit version this project ships.
 *
 * Subclasses:
 *  - provide their first state via [initialState];
 *  - do startup work (collectors, fetches) in [initData], invoked once when the
 *    container is first observed;
 *  - mutate state only inside `intent { reduce { ... } }`;
 *  - emit one-shot effects only through `postSideEffect(...)`.
 *
 * The UI observes [stateFlow] / [sideFlow]; launch arguments are read with
 * [argument] / [argumentNullable].
 */
abstract class BaseViewModel<STATE : Any, SIDE_EFFECT : Any>(
    @PublishedApi internal val savedStateHandle: SavedStateHandle,
) : ViewModel(), ContainerHost<STATE, SIDE_EFFECT> {

    override val container: Container<STATE, SIDE_EFFECT> =
        container(initialState = initialState(), onCreate = { initData() })

    /** The state the container starts in. Called during construction. */
    protected abstract fun initialState(): STATE

    /** Startup work, run lazily once the container is first observed. */
    protected open fun initData() = Unit

    fun stateFlow(): StateFlow<STATE> = container.stateFlow

    fun sideFlow(): Flow<SIDE_EFFECT> = container.sideEffectFlow

    protected inline fun <reified T> argument(key: String? = null) =
        savedStateHandle.argument<T>(key)

    protected inline fun <reified T> argumentNullable(key: String? = null) =
        savedStateHandle.argumentNullable<T>(key)
}
