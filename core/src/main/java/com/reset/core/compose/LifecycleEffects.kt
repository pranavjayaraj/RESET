package com.reset.core.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

/**
 * Collects [flow] while the host is at least [minActiveState], running [action] for each
 * value. The feature guide uses this to drain a ViewModel's `sideFlow()` so one-shot
 * effects (navigation, toasts) are never replayed when the screen comes back to the
 * foreground. Ported from the sharechat `RepeatOnLifeCycle` helper.
 */
@Composable
fun <T> LifecycleAwareLaunchedEffect(
    flow: Flow<T>,
    key1: Any = Unit,
    minActiveState: Lifecycle.State = Lifecycle.State.RESUMED,
    action: suspend CoroutineScope.(T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val flowWithLifecycle = remember(flow, lifecycleOwner) {
        flow.flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState)
    }
    LaunchedEffect(flow, key1) {
        flowWithLifecycle
            .onEach { action(it) }
            .collect()
    }
}
