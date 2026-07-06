package com.reset.feature.sessions.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.reset.core.designsystem.AppColors
import com.reset.core.designsystem.AppShapes
import com.reset.core.designsystem.AppType
import com.reset.feature.sessions.SessionsConstants

/** The Sessions header: the wordmark-styled tab title. */
@Composable
fun SessionsTopBar(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(bottom = SessionsConstants.sectionSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, style = AppType.wordmark, color = AppColors.textPrimary)
    }
}

/** One glass stat tile: big value over a muted mono label. */
@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(AppShapes.quote)
            .background(AppColors.glassFill)
            .border(1.dp, AppColors.glassBorder, AppShapes.quote)
            .padding(SessionsConstants.cardPadding),
        verticalArrangement = Arrangement.spacedBy(SessionsConstants.statValueSpacing),
    ) {
        Text(text = value, style = AppType.bubbleMain, color = AppColors.textPrimary)
        Text(text = label, style = AppType.bubbleSub, color = AppColors.textMuted)
    }
}

/** Shown while the history is empty — the "your first sit" invitation. */
@Composable
fun EmptyHistoryCard(title: String, body: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(AppShapes.banner)
            .background(AppColors.glassFill)
            .border(1.dp, AppColors.glassBorder, AppShapes.banner)
            .padding(SessionsConstants.cardPadding),
        verticalArrangement = Arrangement.spacedBy(SessionsConstants.emptyStateSpacing),
    ) {
        Text(text = title, style = AppType.bannerTitle, color = AppColors.textStrong)
        Text(text = body, style = AppType.bannerBody, color = AppColors.textSecondary)
    }
}

@Composable
fun RetryButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(text = label, style = AppType.bannerButton, color = AppColors.textPrimary)
    }
}
