package com.reset.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.reset.feature.home.AfkActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Launcher shim. The AFK feature owns its own entry Activity, so the app simply hands off
 * to it through the feature's intent factory and finishes.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(AfkActivity.getActivityIntent(this))
        finish()
    }
}
