package com.reset.feature.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.reset.feature.home.R
import com.reset.feature.home.ui.theme.AfkColors

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    val label = stringResource(R.string.afk_loading)
    Box(
        modifier.fillMaxSize().semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = AfkColors.textPrimary)
    }
}
