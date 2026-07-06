package com.reset.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

/**
 * Hosts navigation for the app: collects [Navigator.events] while the host is at least
 * STARTED and applies each to the real [navController]. This is the single place that
 * knows about the `NavController`; features stay unaware of it.
 *
 * @param onExit invoked when there is nothing left to pop (back out of the app).
 */
@Composable
fun ObserveNavigation(
    navigator: Navigator,
    navController: NavController,
    onExit: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(navigator, navController) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            navigator.events.collect { event ->
                when (event) {
                    is NavEvent.Navigate -> navController.navigate(event.screen) {
                        launchSingleTop = true
                    }
                    is NavEvent.SwitchTab -> navController.navigate(event.screen) {
                        // Canonical bottom-bar behaviour: one tab deep at a time, state
                        // preserved per tab, back always lands on the start destination.
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    NavEvent.Pop -> if (!navController.popBackStack()) onExit()
                    is NavEvent.PopTo ->
                        navController.popBackStack(event.screen, inclusive = false)
                    NavEvent.Exit -> onExit()
                }
            }
        }
    }
}
