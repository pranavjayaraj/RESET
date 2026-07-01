package com.reset.feature.home.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.reset.feature.home.HomeConstants
import com.reset.feature.home.R
import com.reset.core.designsystem.BackIcon
import com.reset.core.designsystem.glass
import com.reset.core.designsystem.AppColors
import com.reset.core.designsystem.AppDimens
import com.reset.core.designsystem.AppShapes
import com.reset.core.designsystem.AppType

private const val BREATHE_PERIOD_MS = 9_000

/**
 * The meditation session screen: a calm countdown behind a gently breathing ring.
 * Stateless — the countdown is owned by the ViewModel and passed in as
 * [remainingSeconds]; user actions flow up through [onBack] / [onFinish].
 */
@Composable
fun MeditationScreen(
    durationMin: Int,
    remainingSeconds: Int,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.fillMaxWidth()) {
            val backLabel = stringResource(R.string.afk_back)
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .size(AppDimens.gearSize)
                    .clip(AppShapes.pill)
                    .glass(AppShapes.pill)
                    .clickable(onClick = onBack)
                    .semantics { contentDescription = backLabel },
                contentAlignment = Alignment.Center,
            ) {
                BackIcon(Modifier.size(22.dp), tint = AppColors.textPrimary)
            }
            Text(
                text = stringResource(R.string.afk_meditation_title),
                style = AppType.stubTitle,
                color = AppColors.textPrimary,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        Spacer(Modifier.weight(1f))

        BreathingCountdown(durationMin = durationMin, remainingSeconds = remainingSeconds)

        Text(
            text = stringResource(R.string.afk_meditation_hint),
            style = AppType.bannerBody,
            color = AppColors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp),
        )

        Spacer(Modifier.weight(1f))

        FinishButton(onClick = onFinish, modifier = Modifier.padding(bottom = 8.dp))
    }
}

@Composable
private fun BreathingCountdown(durationMin: Int, remainingSeconds: Int, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "meditation")
    val breathe by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(BREATHE_PERIOD_MS), repeatMode = RepeatMode.Reverse),
        label = "breathe",
    )

    val countdown = HomeConstants.formatMmSs(remainingSeconds)
    val countdownLabel = stringResource(R.string.afk_meditation_countdown_content_description, countdown)

    Box(
        modifier
            .size(AppDimens.bubbleSize)
            .scale(breathe)
            .clip(CircleShape)
            .glass(CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
            .semantics { contentDescription = countdownLabel },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = stringResource(R.string.afk_meditation_breathe),
                style = AppType.bubbleTop,
                color = AppColors.textSecondary,
            )
            Text(text = countdown, style = AppType.bubbleMain, color = AppColors.textPrimary)
            Text(
                text = stringResource(R.string.afk_bubble_sub, durationMin),
                style = AppType.bubbleSub,
                color = AppColors.textTertiary,
            )
        }
    }
}

@Composable
private fun FinishButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(AppShapes.pill)
            .glass(AppShapes.pill)
            .clickable(onClick = onClick)
            .padding(horizontal = 40.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.afk_meditation_finish),
            style = AppType.bannerButton,
            color = AppColors.textPrimary,
        )
    }
}
