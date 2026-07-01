package com.reset.feature.home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.reset.feature.home.R
import com.reset.core.designsystem.AppColors
import com.reset.core.designsystem.AppDimens
import com.reset.core.designsystem.AppShapes
import com.reset.core.designsystem.AppType
import com.reset.core.designsystem.ClosedEyeIcon
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

/** Slide-in reset reminder notification, mirroring the design's banner. */
@Composable
fun ReminderBanner(
    visible: Boolean,
    onLater: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(AppShapes.banner)
                .background(AppColors.bannerScrim)
                .border(1.dp, AppColors.bannerBorder, AppShapes.banner)
                .padding(AppDimens.bannerPadding),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    Modifier.size(22.dp).clip(AppShapes.notifIcon).background(AppColors.accent),
                    contentAlignment = Alignment.Center,
                ) {
                    ClosedEyeIcon(
                        Modifier
                            .size(15.dp)
                            .semantics { contentDescription = "" },
                        tint = AppColors.textPrimary,
                    )
                }
                Text(
                    stringResource(R.string.afk_banner_app).uppercase(),
                    style = AppType.bannerApp,
                    color = AppColors.textTertiary,
                )
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.afk_banner_now), style = AppType.bannerTime, color = AppColors.textFaint)
            }
            Spacer(Modifier.height(7.dp))
            Text(stringResource(R.string.afk_banner_title), style = AppType.bannerTitle, color = AppColors.textPrimary)
            Spacer(Modifier.height(3.dp))
            Text(stringResource(R.string.afk_banner_body), style = AppType.bannerBody, color = AppColors.textSecondary)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BannerButton(
                    text = stringResource(R.string.afk_banner_later),
                    primary = false,
                    onClick = onLater,
                    modifier = Modifier.weight(1f),
                )
                BannerButton(
                    text = stringResource(R.string.afk_banner_reset),
                    primary = true,
                    onClick = onReset,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BannerButton(
    text: String,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .height(AppDimens.touchTargetMin)
            .clip(AppShapes.bannerButton)
            .then(
                if (primary) {
                    Modifier.background(AppColors.textPrimary)
                } else {
                    Modifier.glass(AppShapes.bannerButton)
                },
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = AppType.bannerButton,
            color = if (primary) AppColors.onDeep else AppColors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}
