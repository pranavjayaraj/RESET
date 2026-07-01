package com.reset.feature.home.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reset.core.compose.LifecycleAwareLaunchedEffect
import com.reset.feature.home.HomeState
import com.reset.feature.home.HomeViewModel
import com.reset.feature.home.R
import com.reset.model.domain.SoundController
import com.reset.feature.home.ui.ErrorScreen
import com.reset.feature.home.ui.HomeScreen
import com.reset.feature.home.ui.LoadingScreen
import com.reset.feature.home.ui.MeditationScreen
import com.reset.feature.home.ui.StubScreen
import com.reset.feature.home.ui.components.ConcentricArcs
import com.reset.feature.home.ui.theme.HomeColors
import com.reset.feature.home.ui.theme.HomeDimens
import androidx.compose.ui.res.stringResource

/**
 * Hosts the AFK flow. State is rendered per-route via the [NavHost]; the destination is
 * driven entirely by navigation [HomeSideEffect]s posted from [HomeViewModel] and applied
 * through [HomeNavigationAction]. Back-press is owned by the ViewModel — the UI only
 * dispatches [HomeIntent.HandleBackPress].
 */
@Composable
fun HomeNavGraph(
    viewModel: () -> HomeViewModel,
    soundController: SoundController,
    closeActivity: () -> Unit,
) {
    val navController = rememberNavController()
    val navActions: HomeNavigationAction = remember(navController) {
        HomeNavigationActionImpl(navController)
    }

    val state by viewModel().stateFlow().collectAsStateWithLifecycle()

    CompositionLocalProvider(LocalHomeNavigationAction provides navActions) {
        HandleHomeSideEffects(
            viewModel = viewModel(),
            soundController = soundController,
            closeActivity = closeActivity,
        )

        HomeBackground {
            NavHost(
                navController = navController,
                startDestination = HomeNavigationRoutes.HomeScreen.route,
            ) {
                composable(route = HomeNavigationRoutes.LoadingScreen.route) {
                    LoadingScreen()
                }

                composable(route = HomeNavigationRoutes.HomeScreen.route) {
                    HandleBackPress(viewModel)
                    HomeScreen(state = state, onIntent = viewModel()::handleHomeIntent)
                }

                composable(route = HomeNavigationRoutes.SessionScreen.route) {
                    HandleBackPress(viewModel)
                    MeditationScreen(
                        durationMin = state.durationMin,
                        remainingSeconds = state.remainingSeconds,
                        onBack = { viewModel().handleHomeIntent(HomeIntent.HandleBackPress) },
                        onFinish = { viewModel().handleHomeIntent(HomeIntent.FinishSession) },
                    )
                }

                composable(route = HomeNavigationRoutes.SettingsScreen.route) {
                    HandleBackPress(viewModel)
                    StubScreen(
                        title = stringResource(R.string.afk_settings_title),
                        onBack = { viewModel().handleHomeIntent(HomeIntent.HandleBackPress) },
                    )
                }

                composable(route = HomeNavigationRoutes.ErrorScreen.route) {
                    HandleBackPress(viewModel)
                    ErrorScreen(onRetry = { viewModel().handleHomeIntent(HomeIntent.Retry) })
                }
            }
        }
    }
}

@Composable
private fun HandleHomeSideEffects(
    viewModel: HomeViewModel,
    soundController: SoundController,
    closeActivity: () -> Unit,
) {
    val navigation = LocalHomeNavigationAction.current
    LifecycleAwareLaunchedEffect(viewModel.sideFlow()) { effect ->
        when (effect) {
            HomeSideEffect.ShowLoadingScreen -> navigation.openLoadingScreen()
            HomeSideEffect.ShowHomeScreen -> navigation.openHomeScreen()
            HomeSideEffect.ShowSessionScreen -> navigation.openSessionScreen()
            HomeSideEffect.ShowSettingsScreen -> navigation.openSettingsScreen()
            HomeSideEffect.ShowErrorScreen -> navigation.openErrorScreen()
            HomeSideEffect.NavigateHome -> navigation.backToHome()
            HomeSideEffect.CloseActivity -> closeActivity()
            is HomeSideEffect.PlayChime -> soundController.playChime(effect.kind)
        }
    }
}

@Composable
private fun HandleBackPress(viewModel: () -> HomeViewModel) {
    BackHandler {
        viewModel().handleHomeIntent(HomeIntent.HandleBackPress)
    }
}

@Composable
private fun HomeBackground(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize().background(HomeColors.restGradient)) {
        ConcentricArcs(Modifier.matchParentSize())
        Box(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = HomeDimens.screenPaddingH, vertical = HomeDimens.screenPaddingV),
        ) {
            content()
        }
    }
}
