package com.reset.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.reset.feature.home.R
import com.reset.feature.home.ui.theme.AfkColors
import com.reset.feature.home.ui.theme.AfkDimens
import com.reset.feature.home.ui.theme.AfkShapes
import com.reset.feature.home.ui.theme.AfkType

@Composable
fun ErrorScreen(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.afk_error_title),
            style = AfkType.errorTitle,
            color = AfkColors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Box(
            Modifier
                .padding(top = 16.dp)
                .height(AfkDimens.touchTargetMin)
                .clip(AfkShapes.banner)
                .background(AfkColors.textPrimary)
                .clickable(onClick = onRetry),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.afk_retry),
                style = AfkType.bannerButton,
                color = AfkColors.onDeep,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}
