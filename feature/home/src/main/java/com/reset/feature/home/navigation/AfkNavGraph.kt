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
import com.reset.feature.home.AfkState
import com.reset.feature.home.AfkViewModel
import com.reset.feature.home.R
import com.reset.model.domain.SoundController
import com.reset.feature.home.ui.ErrorScreen
import com.reset.feature.home.ui.HomeScreen
import com.reset.feature.home.ui.LoadingScreen
import com.reset.feature.home.ui.StubScreen
import com.reset.feature.home.ui.components.ConcentricArcs
import com.reset.feature.home.ui.theme.AfkColors
import com.reset.feature.home.ui.theme.AfkDimens
import androidx.compose.ui.res.stringResource

/**
 * Hosts the AFK flow. State is rendered per-route via the [NavHost]; the destination is
 * driven entirely by navigation [AfkSideEffect]s posted from [AfkViewModel] and applied
 * through [AfkNavigationAction]. Back-press is owned by the ViewModel — the UI only
 * dispatches [AfkIntent.HandleBackPress].
 */
@Composable
fun AfkNavGraph(
    viewModel: () -> AfkViewModel,
    soundController: SoundController,
    closeActivity: () -> Unit,
) {
    val navController = rememberNavController()
    val navActions: AfkNavigationAction = remember(navController) {
        AfkNavigationActionImpl(navController)
    }

    val state by viewModel().stateFlow().collectAsStateWithLifecycle()

    CompositionLocalProvider(LocalAfkNavigationAction provides navActions) {
        HandleAfkSideEffects(
            viewModel = viewModel(),
            soundController = soundController,
            closeActivity = closeActivity,
        )

        AfkBackground {
            NavHost(
                navController = navController,
                startDestination = AfkNavigationRoutes.HomeScreen.route,
            ) {
                composable(route = AfkNavigationRoutes.LoadingScreen.route) {
                    LoadingScreen()
                }

                composable(route = AfkNavigationRoutes.HomeScreen.route) {
                    HandleBackPress(viewModel)
                    HomeScreen(state = state, onIntent = viewModel()::handleAfkIntent)
                }

                composable(route = AfkNavigationRoutes.SessionScreen.route) {
                    HandleBackPress(viewModel)
                    StubScreen(
                        title = stringResource(R.string.afk_reset),
                        onBack = { viewModel().handleAfkIntent(AfkIntent.HandleBackPress) },
                    )
                }

                composable(route = AfkNavigationRoutes.SettingsScreen.route) {
                    HandleBackPress(viewModel)
                    StubScreen(
                        title = stringResource(R.string.afk_settings_title),
                        onBack = { viewModel().handleAfkIntent(AfkIntent.HandleBackPress) },
                    )
                }

                composable(route = AfkNavigationRoutes.ErrorScreen.route) {
                    HandleBackPress(viewModel)
                    ErrorScreen(onRetry = { viewModel().handleAfkIntent(AfkIntent.Retry) })
                }
            }
        }
    }
}

@Composable
private fun HandleAfkSideEffects(
    viewModel: AfkViewModel,
    soundController: SoundController,
    closeActivity: () -> Unit,
) {
    val navigation = LocalAfkNavigationAction.current
    LifecycleAwareLaunchedEffect(viewModel.sideFlow()) { effect ->
        when (effect) {
            AfkSideEffect.ShowLoadingScreen -> navigation.openLoadingScreen()
            AfkSideEffect.ShowHomeScreen -> navigation.openHomeScreen()
            AfkSideEffect.ShowSessionScreen -> navigation.openSessionScreen()
            AfkSideEffect.ShowSettingsScreen -> navigation.openSettingsScreen()
            AfkSideEffect.ShowErrorScreen -> navigation.openErrorScreen()
            AfkSideEffect.NavigateHome -> navigation.backToHome()
            AfkSideEffect.CloseActivity -> closeActivity()
            is AfkSideEffect.PlayChime -> soundController.playChime(effect.kind)
        }
    }
}

@Composable
private fun HandleBackPress(viewModel: () -> AfkViewModel) {
    BackHandler {
        viewModel().handleAfkIntent(AfkIntent.HandleBackPress)
    }
}

@Composable
private fun AfkBackground(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize().background(AfkColors.restGradient)) {
        ConcentricArcs(Modifier.matchParentSize())
        Box(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = AfkDimens.screenPaddingH, vertical = AfkDimens.screenPaddingV),
        ) {
            content()
        }
    }
}
