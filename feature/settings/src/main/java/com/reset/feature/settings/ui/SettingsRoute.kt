package com.reset.feature.settings.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reset.core.compose.LifecycleAwareLaunchedEffect
import com.reset.feature.settings.SettingsViewModel
import com.reset.feature.settings.navigation.SettingsIntent
import com.reset.feature.settings.navigation.SettingsSideEffect

/**
 * Composable entry point for the Settings feature, placed into the app's NavHost. Back and
 * Done both pop via the ViewModel's injected [com.reset.navigation.Navigator]; the screen
 * renders on the app-provided [com.reset.core.designsystem.AppBackground].
 *
 * The POST_NOTIFICATIONS launcher lives here (lifecycle-aware, auto-unregistered) and its
 * result flows back to the ViewModel as an ordinary typed intent.
 */
@Composable
fun SettingsRoute() {
    val viewModel: SettingsViewModel = hiltViewModel()
    val state by viewModel.stateFlow().collectAsStateWithLifecycle()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        viewModel.handleSettingsIntent(SettingsIntent.UpdateNotificationPermission(granted))
    }

    LifecycleAwareLaunchedEffect(viewModel.sideFlow()) { effect ->
        when (effect) {
            SettingsSideEffect.RequestNotificationPermission ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    // No runtime permission below API 33 — report granted straight back.
                    viewModel.handleSettingsIntent(SettingsIntent.UpdateNotificationPermission(true))
                }
        }
    }

    BackHandler { viewModel.handleSettingsIntent(SettingsIntent.HandleBackPress) }

    SettingsScreen(state = state, onIntent = viewModel::handleSettingsIntent)
}
