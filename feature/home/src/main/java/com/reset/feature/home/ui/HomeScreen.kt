package com.reset.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.reset.feature.home.AfkConstants
import com.reset.feature.home.AfkState
import com.reset.feature.home.R
import com.reset.feature.home.navigation.AfkIntent
import com.reset.model.domain.model.AfkPreferences
import com.reset.model.domain.model.SessionStats
import com.reset.feature.home.ui.components.BreathingBubble
import com.reset.feature.home.ui.components.DurationSelector
import com.reset.feature.home.ui.components.GearIconButton
import com.reset.feature.home.ui.components.QuoteCard
import com.reset.feature.home.ui.components.ReminderBanner
import com.reset.feature.home.ui.theme.AfkColors
import com.reset.feature.home.ui.theme.AfkType

@Composable
fun HomeScreen(
    state: AfkState,
    onIntent: (AfkIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val factTexts = AfkConstants.factTexts
    val factSources = AfkConstants.factSources
    val resetWords = AfkConstants.resetWords
    val factIndex = AfkConstants.getFactIndex(state)

    Box(modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.fillMaxWidth()) {
                GearIconButton(
                    onClick = { onIntent(AfkIntent.OpenSettings) },
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }

            QuoteCard(text = factTexts[factIndex], source = factSources[factIndex])

            Spacer(Modifier.weight(0.4f))

            DurationSelector(
                options = AfkPreferences.DURATION_OPTIONS,
                selected = state.durationMin,
                onSelect = { onIntent(AfkIntent.SelectDuration(it)) },
            )

            Spacer(Modifier.weight(1f))

            BreathingBubble(
                topLabel = stringResource(R.string.afk_close_your_eyes),
                title = stringResource(R.string.afk_reset),
                subLabel = stringResource(R.string.afk_bubble_sub, state.durationMin),
                contentDescription = pluralStringResource(
                    R.plurals.afk_reset_content_description,
                    state.durationMin,
                    state.durationMin,
                ),
                words = resetWords,
                leaving = state.leaving,
                onReset = { onIntent(AfkIntent.TapReset) },
            )

            Spacer(Modifier.weight(1f))

            FooterStats(stats = state.stats)
        }

        ReminderBanner(
            visible = state.showReminderBanner,
            onLater = { onIntent(AfkIntent.DismissReminderBanner) },
            onReset = { onIntent(AfkIntent.TapReset) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun FooterStats(stats: SessionStats, modifier: Modifier = Modifier) {
    val text = if (stats.isFirstSit) {
        stringResource(R.string.afk_footer_first_sit)
    } else {
        val sits = pluralStringResource(R.plurals.afk_sits_count, stats.sessions, stats.sessions)
        stringResource(R.string.afk_footer_stats, sits, stats.streak.coerceAtLeast(1))
    }
    Text(
        text = text,
        style = AfkType.footer,
        color = AfkColors.textFaint,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth().padding(top = 8.dp),
    )
}
