package com.reset.feature.sessions.api

import com.reset.navigation.Screen
import kotlinx.serialization.Serializable

/**
 * The Sessions feature's navigable destination — the middle tab of the app's dashboard.
 * Living in the feature's `api` submodule, it is the only thing another feature (or the
 * app host's bottom bar) needs to navigate here — implementations stay in the sibling impl
 * module, so destination churn there never recompiles callers. The type itself is the
 * route (Navigation 2.8 type-safe DSL).
 */
@Serializable
data object SessionsDestination : Screen
