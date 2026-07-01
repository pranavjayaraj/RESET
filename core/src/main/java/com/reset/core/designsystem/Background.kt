package com.reset.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * The app's full-bleed rest gradient with faint concentric arcs behind it, then the
 * screen content inset for system bars and the standard horizontal/vertical padding.
 * Shared so every feature surface renders on the same backdrop.
 */
@Composable
fun AppBackground(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize().background(AppColors.restGradient)) {
        ConcentricArcs(Modifier.matchParentSize())
        Box(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = AppDimens.screenPaddingH, vertical = AppDimens.screenPaddingV),
        ) {
            content()
        }
    }
}
