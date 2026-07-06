package com.reset.app.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.reset.app.R
import com.reset.core.designsystem.AppColors
import com.reset.core.designsystem.AppDimens
import com.reset.core.designsystem.AppShapes
import com.reset.core.designsystem.AppType
import com.reset.core.designsystem.HomeIcon
import com.reset.core.designsystem.ProfileIcon
import com.reset.core.designsystem.RippleIcon
import com.reset.feature.home.api.HomeDestination
import com.reset.feature.sessions.api.SessionsDestination
import com.reset.feature.settings.api.SettingsDestination
import com.reset.navigation.Screen

/**
 * The dashboard's three tabs. App-owned chrome: only the host knows the graph, so only it
 * may enumerate top-level destinations. Ordering here is the on-screen ordering.
 */
private enum class DashboardTab(@StringRes val labelRes: Int) {
    Home(R.string.tab_home),
    Sessions(R.string.tab_sessions),
    Profile(R.string.tab_profile);

    val destination: Screen
        get() = when (this) {
            Home -> HomeDestination
            Sessions -> SessionsDestination
            Profile -> SettingsDestination
        }
}

private val barItemIconSize = 22.dp
private val barPaddingV = 8.dp
private val barMarginTop = 10.dp
private val barLabelSpacing = 3.dp

/**
 * Glass bottom bar hosting the app's three top-level tabs (Home / Sessions / Profile).
 * Selection is derived from the [navController]'s current entry; taps flow through
 * [onSelectTab] into the Navigator seam so tab changes ride the same event stream as
 * every other navigation (single pipeline, loggable in ObserveNavigation).
 */
@Composable
fun ResetBottomBar(
    navController: NavController,
    onSelectTab: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val selected = when {
        backStackEntry?.destination?.hasRoute<SessionsDestination>() == true -> DashboardTab.Sessions
        backStackEntry?.destination?.hasRoute<SettingsDestination>() == true -> DashboardTab.Profile
        else -> DashboardTab.Home
    }

    Row(
        modifier
            .fillMaxWidth()
            .padding(top = barMarginTop)
            .clip(AppShapes.pill)
            .background(AppColors.glassFill)
            .border(1.dp, AppColors.glassBorder, AppShapes.pill)
            .padding(vertical = barPaddingV),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DashboardTab.entries.forEach { tab ->
            BarItem(
                tab = tab,
                isSelected = tab == selected,
                onClick = { if (tab != selected) onSelectTab(tab.destination) },
            )
        }
    }
}

@Composable
private fun BarItem(tab: DashboardTab, isSelected: Boolean, onClick: () -> Unit) {
    val tint = if (isSelected) AppColors.textPrimary else AppColors.textMuted
    Column(
        Modifier
            .heightIn(min = AppDimens.touchTargetMin)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(barLabelSpacing, Alignment.CenterVertically),
    ) {
        TabIcon(tab, tint)
        Text(text = stringResource(tab.labelRes), style = AppType.bubbleSub, color = tint)
    }
}

@Composable
private fun TabIcon(tab: DashboardTab, tint: Color) {
    val iconModifier = Modifier.size(barItemIconSize)
    when (tab) {
        DashboardTab.Home -> HomeIcon(iconModifier, tint)
        DashboardTab.Sessions -> RippleIcon(iconModifier, tint)
        DashboardTab.Profile -> ProfileIcon(iconModifier, tint)
    }
}
