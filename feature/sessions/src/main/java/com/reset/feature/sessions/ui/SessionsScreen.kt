package com.reset.feature.sessions.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.reset.core.designsystem.AppColors
import com.reset.core.designsystem.AppType
import com.reset.feature.sessions.R
import com.reset.feature.sessions.SessionsConstants
import com.reset.feature.sessions.SessionsState
import com.reset.feature.sessions.SessionsStatus
import com.reset.feature.sessions.navigation.SessionsIntent
import com.reset.feature.sessions.ui.components.EmptyHistoryCard
import com.reset.feature.sessions.ui.components.RetryButton
import com.reset.feature.sessions.ui.components.SessionsTopBar
import com.reset.feature.sessions.ui.components.StatCard

/**
 * Stateless Sessions surface: the sit-history dashboard tab. Renders purely from
 * [SessionsState]; every event flows up through [onIntent].
 */
@Composable
fun SessionsScreen(
    state: SessionsState,
    onIntent: (SessionsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        SessionsTopBar(title = stringResource(R.string.sessions_title))

        when (state.status) {
            // The bare AppBackground gradient is the loading state, matching Home.
            SessionsStatus.Loading -> Unit
            is SessionsStatus.Error -> ErrorContent(onRetry = { onIntent(SessionsIntent.Retry) })
            SessionsStatus.Content -> HistoryContent(state)
        }
    }
}

@Composable
private fun HistoryContent(state: SessionsState) {
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(SessionsConstants.cardSpacing),
    ) {
        Text(
            text = stringResource(R.string.sessions_section_history),
            style = AppType.bubbleTop,
            color = AppColors.textMuted,
            modifier = Modifier.padding(bottom = SessionsConstants.statValueSpacing),
        )

        if (state.stats.isFirstSit) {
            EmptyHistoryCard(
                title = stringResource(R.string.sessions_empty_title),
                body = stringResource(R.string.sessions_empty_body),
            )
        } else {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SessionsConstants.cardSpacing),
            ) {
                StatCard(
                    value = state.stats.sessions.toString(),
                    label = stringResource(R.string.sessions_stat_sits),
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = SessionsConstants.formatTotalMinutes(state.stats.totalMin),
                    label = stringResource(R.string.sessions_stat_time),
                    modifier = Modifier.weight(1f),
                )
            }
            StatCard(
                value = state.stats.streak.toString(),
                label = stringResource(R.string.sessions_stat_streak),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ErrorContent(onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SessionsConstants.emptyStateSpacing),
        ) {
            Text(
                text = stringResource(R.string.sessions_error_title),
                style = AppType.errorTitle,
                color = AppColors.textStrong,
            )
            RetryButton(label = stringResource(R.string.sessions_retry), onClick = onRetry)
        }
    }
}
