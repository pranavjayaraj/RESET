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
import com.reset.feature.home.ui.theme.HomeColors
import com.reset.feature.home.ui.theme.HomeDimens
import com.reset.feature.home.ui.theme.HomeShapes
import com.reset.feature.home.ui.theme.HomeType

/** 48dp glass settings button with the spoke gear icon. */
@Composable
fun GearIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.afk_settings)
    Box(
        modifier
            .size(HomeDimens.gearSize)
            .clip(HomeShapes.pill)
            .glass(HomeShapes.pill)
            .clickable(onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        GearIcon(Modifier.size(22.dp), tint = HomeColors.textPrimary)
    }
}

/** Glass card carrying the rotating eye-science quote. */
@Composable
fun QuoteCard(text: String, source: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .glass(HomeShapes.quote)
            .padding(HomeDimens.quotePadding),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Text(text, style = HomeType.quoteText, color = HomeColors.textStrong)
        Text(source, style = HomeType.quoteSource, color = HomeColors.textFaint)
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
        modifier.glass(HomeShapes.pill).padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        options.forEach { minutes ->
            val active = minutes == selected
            val label = pluralStringResource(R.plurals.afk_select_duration_content_description, minutes, minutes)
            Row(
                Modifier
                    .clip(HomeShapes.pill)
                    .then(if (active) Modifier.background(HomeColors.glassActiveFill, HomeShapes.pill) else Modifier)
                    .clickable { onSelect(minutes) }
                    .defaultMinSize(minHeight = HomeDimens.touchTargetMin)
                    .padding(horizontal = 26.dp, vertical = 9.dp)
                    .clearAndSetSemantics { contentDescription = label },
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = minutes.toString(),
                    style = HomeType.segValue,
                    color = if (active) HomeColors.on else HomeColors.textSecondary,
                )
                Text(
                    text = unit,
                    style = HomeType.segUnit,
                    color = if (active) HomeColors.on else HomeColors.textTertiary,
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
                .clip(HomeShapes.banner)
                .background(HomeColors.bannerScrim)
                .border(1.dp, HomeColors.bannerBorder, HomeShapes.banner)
                .padding(HomeDimens.bannerPadding),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    Modifier.size(22.dp).clip(HomeShapes.notifIcon).background(HomeColors.accent),
                    contentAlignment = Alignment.Center,
                ) {
                    ClosedEyeIcon(
                        Modifier
                            .size(15.dp)
                            .semantics { contentDescription = "" },
                        tint = HomeColors.textPrimary,
                    )
                }
                Text(
                    stringResource(R.string.afk_banner_app).uppercase(),
                    style = HomeType.bannerApp,
                    color = HomeColors.textTertiary,
                )
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.afk_banner_now), style = HomeType.bannerTime, color = HomeColors.textFaint)
            }
            Spacer(Modifier.height(7.dp))
            Text(stringResource(R.string.afk_banner_title), style = HomeType.bannerTitle, color = HomeColors.textPrimary)
            Spacer(Modifier.height(3.dp))
            Text(stringResource(R.string.afk_banner_body), style = HomeType.bannerBody, color = HomeColors.textSecondary)
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
            .height(HomeDimens.touchTargetMin)
            .clip(HomeShapes.bannerButton)
            .then(
                if (primary) {
                    Modifier.background(HomeColors.textPrimary)
                } else {
                    Modifier.glass(HomeShapes.bannerButton)
                },
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = HomeType.bannerButton,
            color = if (primary) HomeColors.onDeep else HomeColors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}
