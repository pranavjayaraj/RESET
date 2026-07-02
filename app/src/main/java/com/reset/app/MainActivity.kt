package com.reset.app

import android.content.Intent
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
import com.reset.model.domain.ReminderAction
import com.reset.model.domain.ReminderActionStore
import com.reset.model.domain.SoundController
import com.reset.navigation.AppDestination
import com.reset.repository.notification.NotificationConstants
import com.reset.repository.notification.ReminderNotificationUtil
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

    @Inject
    lateinit var reminderActionStore: ReminderActionStore

    @Inject
    lateinit var reminderNotificationUtil: ReminderNotificationUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        handleReminderAction(intent)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleReminderAction(intent)
    }

    /**
     * Translates the reminder's "Reset" action into pure data for the feature layer:
     * the notification is cleared (action buttons don't auto-cancel) and a
     * [ReminderAction.StartReset] is dispatched for the Home ViewModel to collect.
     * The store is buffered, so a cold-start dispatch waits for the collector.
     */
    private fun handleReminderAction(intent: Intent?) {
        if (intent?.action != NotificationConstants.ACTION_START_RESET) return
        reminderNotificationUtil.cancelReminderNotification()
        reminderActionStore.dispatch(ReminderAction.StartReset)
    }
}
