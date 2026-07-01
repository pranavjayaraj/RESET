package com.reset.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reset.core.designsystem.AppBackground
import com.reset.feature.home.ui.HomeRoute
import com.reset.feature.settings.ui.SettingsRoute
import com.reset.model.domain.SoundController
import com.reset.navigation.AppDestination
import com.reset.navigation.Navigator
import com.reset.navigation.ObserveNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * The single host Activity. It owns the [NavHost] and is the only place that knows the
 * navigation graph: features emit events through the injected [Navigator] and
 * [ObserveNavigation] applies them here. [SoundController] is handed to the Home route so
 * the feature can play its chime without owning the audio wiring.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var soundController: SoundController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                ObserveNavigation(
                    navigator = navigator,
                    navController = navController,
                    onExit = { finish() },
                )
                AppBackground {
                    NavHost(
                        navController = navController,
                        startDestination = AppDestination.Home.route,
                    ) {
                        
                        composable(AppDestination.Home.route) { HomeRoute(soundController) }
                        
                        composable(AppDestination.Settings.route) { SettingsRoute() }
                    }
                }
            }
        }
    }
}
