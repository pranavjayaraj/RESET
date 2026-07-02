package com.reset.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.reset.feature.home.R
import com.reset.core.designsystem.AppColors
import com.reset.core.designsystem.AppDimens
import com.reset.core.designsystem.AppShapes
import com.reset.core.designsystem.AppType
import com.reset.core.designsystem.GearIcon
import com.reset.core.designsystem.glass

/** 48dp glass settings button with the spoke gear icon. */
@Composable
fun GearIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.afk_settings)
    Box(
        modifier
            .size(AppDimens.gearSize)
            .clip(AppShapes.pill)
            .glass(AppShapes.pill)
            .clickable(onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        GearIcon(Modifier.size(22.dp), tint = AppColors.textPrimary)
    }
}

/** Glass card carrying the rotating eye-science quote. */
@Composable
fun QuoteCard(text: String, source: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .glass(AppShapes.quote)
            .padding(AppDimens.quotePadding),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Text(text, style = AppType.quoteText, color = AppColors.textStrong)
        Text(source, style = AppType.quoteSource, color = AppColors.textFaint)
    }
}

/** Segmented 3 / 5-minute duration control. */
@Composable
fun DurationSelector(
    options: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val unit = stringResource(R.string.afk_minutes_unit)
    Row(
        modifier.glass(AppShapes.pill).padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        options.forEach { minutes ->
            val active = minutes == selected
            val label = pluralStringResource(R.plurals.afk_select_duration_content_description, minutes, minutes)
            Row(
                Modifier
                    .clip(AppShapes.pill)
                    .then(if (active) Modifier.background(AppColors.glassActiveFill, AppShapes.pill) else Modifier)
                    .clickable { onSelect(minutes) }
                    .defaultMinSize(minHeight = AppDimens.touchTargetMin)
                    .padding(horizontal = 26.dp, vertical = 9.dp)
                    .clearAndSetSemantics { contentDescription = label },
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = minutes.toString(),
                    style = AppType.segValue,
                    color = if (active) AppColors.on else AppColors.textSecondary,
                )
                Text(
                    text = unit,
                    style = AppType.segUnit,
                    color = if (active) AppColors.on else AppColors.textTertiary,
                )
            }
        }
    }
}
