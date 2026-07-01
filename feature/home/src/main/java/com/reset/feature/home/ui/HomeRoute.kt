package com.reset.feature.home.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reset.core.compose.LifecycleAwareLaunchedEffect
import com.reset.model.domain.SoundController
import com.reset.feature.home.HomeStatus
import com.reset.feature.home.HomeStep
import com.reset.feature.home.HomeViewModel
import com.reset.feature.home.navigation.HomeIntent
import com.reset.feature.home.navigation.HomeSideEffect

/**
 * Composable entry point for the Home feature, placed into the app's NavHost. Home↔Session
 * is an intra-feature state transition (rendered from [HomeStep]); cross-feature navigation
 * and app-exit go through the ViewModel's injected [com.reset.navigation.Navigator]. The
 * chime is the feature's only one-shot effect, played via the injected [soundController].
 */
@Composable
fun HomeRoute(soundController: SoundController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.stateFlow().collectAsStateWithLifecycle()

    LifecycleAwareLaunchedEffect(viewModel.sideFlow()) { effect ->
        when (effect) {
            is HomeSideEffect.PlayChime -> soundController.playChime(effect.kind)
        }
    }

    BackHandler { viewModel.handleHomeIntent(HomeIntent.HandleBackPress) }

    when (state.status) {
        HomeStatus.Loading -> LoadingScreen()
        is HomeStatus.Error -> ErrorScreen(onRetry = { viewModel.handleHomeIntent(HomeIntent.Retry) })
        HomeStatus.Content -> when (state.screen) {
            HomeStep.Home -> HomeScreen(state = state, onIntent = viewModel::handleHomeIntent)
            HomeStep.Session -> MeditationScreen(
                durationMin = state.durationMin,
                remainingSeconds = state.remainingSeconds,
                onBack = { viewModel.handleHomeIntent(HomeIntent.HandleBackPress) },
                onFinish = { viewModel.handleHomeIntent(HomeIntent.FinishSession) },
            )
        }
    }
}
