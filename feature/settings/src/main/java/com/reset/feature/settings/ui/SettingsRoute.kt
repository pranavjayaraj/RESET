package com.reset.feature.settings.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reset.feature.settings.SettingsViewModel
import com.reset.feature.settings.navigation.SettingsIntent

/**
 * Composable entry point for the Settings feature, placed into the app's NavHost. Back and
 * Done both pop via the ViewModel's injected [com.reset.navigation.Navigator]; the screen
 * renders on the app-provided [com.reset.core.designsystem.AppBackground].
 */
@Composable
fun SettingsRoute() {
    val viewModel: SettingsViewModel = hiltViewModel()
    val state by viewModel.stateFlow().collectAsStateWithLifecycle()

    BackHandler { viewModel.handleSettingsIntent(SettingsIntent.HandleBackPress) }

    SettingsScreen(state = state, onIntent = viewModel::handleSettingsIntent)
}
