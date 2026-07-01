package com.reset.feature.settings.navigation

/**
 * Settings has no one-shot effects — navigation (including closing) goes through the
 * injected [com.reset.navigation.Navigator]. Kept as an empty type to satisfy the
 * `BaseViewModel<State, SideEffect>` contract.
 */
sealed interface SettingsSideEffect
