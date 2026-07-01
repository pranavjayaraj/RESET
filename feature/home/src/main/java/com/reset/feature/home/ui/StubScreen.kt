package com.reset.feature.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.reset.feature.home.R
import com.reset.feature.home.ui.components.BackIcon
import com.reset.feature.home.ui.components.glass
import com.reset.feature.home.ui.theme.HomeColors
import com.reset.feature.home.ui.theme.HomeDimens
import com.reset.feature.home.ui.theme.HomeShapes
import com.reset.feature.home.ui.theme.HomeType

/** Placeholder for screens not yet built in this milestone (Session / Settings). */
@Composable
fun StubScreen(title: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth()) {
            val backLabel = stringResource(R.string.afk_back)
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .size(HomeDimens.gearSize)
                    .clip(HomeShapes.pill)
                    .glass(HomeShapes.pill)
                    .clickable(onClick = onBack)
                    .semantics { contentDescription = backLabel },
                contentAlignment = Alignment.Center,
            ) {
                BackIcon(Modifier.size(22.dp), tint = HomeColors.textPrimary)
            }
            Text(
                text = title,
                style = HomeType.stubTitle,
                color = HomeColors.textPrimary,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.afk_coming_soon),
                style = HomeType.bannerTitle,
                color = HomeColors.textSecondary,
            )
        }
    }
}
