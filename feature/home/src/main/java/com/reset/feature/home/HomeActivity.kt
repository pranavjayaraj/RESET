package com.reset.feature.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.core.view.WindowCompat
import com.reset.model.domain.SoundController
import com.reset.feature.home.navigation.HomeNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Entry point for the AFK feature. Construct its [Intent] only through [getActivityIntent]
 * — never at the call site. The Activity hosts Compose and forwards lifecycle/result
 * callbacks into [HomeNavGraph]; [SoundController] is injected here so chime side effects
 * are handled in the UI layer.
 */
@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    @Inject
    lateinit var soundController: SoundController

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            // AFK draws its own full-bleed gradient surface; MaterialTheme only supplies
            // text/indicator defaults.
            MaterialTheme {
                HomeNavGraph(
                    viewModel = { viewModel },
                    soundController = soundController,
                    closeActivity = { finish() },
                )
            }
        }
    }

    companion object {
        fun getActivityIntent(context: Context): Intent =
            Intent(context, HomeActivity::class.java)
    }
}
