package com.reset.feature.sessions.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reset.feature.sessions.SessionsViewModel
import com.reset.feature.sessions.navigation.SessionsIntent

/**
 * Composable entry point for the Sessions feature, placed into the app's NavHost as the
 * dashboard's middle tab. Back pops via the ViewModel's injected
 * [com.reset.navigation.Navigator]; the screen renders on the app-provided
 * [com.reset.core.designsystem.AppBackground].
 */
@Composable
fun SessionsRoute() {
    val viewModel: SessionsViewModel = hiltViewModel()
    val state by viewModel.stateFlow().collectAsStateWithLifecycle()

    BackHandler { viewModel.handleSessionsIntent(SessionsIntent.HandleBackPress) }

    SessionsScreen(state = state, onIntent = viewModel::handleSessionsIntent)
}
