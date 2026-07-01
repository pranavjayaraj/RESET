package com.reset.feature.settings.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.reset.core.designsystem.AppColors
import com.reset.core.designsystem.AppDimens
import com.reset.core.designsystem.AppShapes
import com.reset.core.designsystem.AppType
import com.reset.core.designsystem.BackIcon
import com.reset.core.designsystem.glass
import com.reset.feature.settings.R
import com.reset.feature.settings.SettingsConstants

/** Back button + centred title, mirroring the design's settings header. */
@Composable
fun SettingsTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth()) {
        val backLabel = stringResource(R.string.settings_back)
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
            text = stringResource(R.string.settings_title),
            style = AppType.stubTitle,
            color = AppColors.textPrimary,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

/** Small uppercase section label. */
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = AppType.bannerApp,
        color = AppColors.textMuted,
        modifier = modifier.padding(top = SettingsConstants.sectionSpacing, bottom = 4.dp),
    )
}

/** Master reminders card: title + live summary on the left, a switch on the right. */
@Composable
fun ReminderMasterCard(
    title: String,
    summary: String,
    enabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .glass(AppShapes.quote)
            .padding(AppDimens.quotePadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, style = AppType.bannerTitle, color = AppColors.textPrimary)
            Text(summary, style = AppType.bubbleSub, color = AppColors.textFaint)
        }
        Spacer(Modifier.width(12.dp))
        ReminderSwitch(on = enabled, onToggle = onToggle)
    }
}

/** Pill toggle: green track + sliding knob when on, translucent when off. */
@Composable
fun ReminderSwitch(on: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.settings_toggle_content_description)
    val trackColor by animateColorAsState(
        targetValue = if (on) AppColors.accent else Color.White.copy(alpha = 0.22f),
        label = "switchTrack",
    )
    val knobOffset by animateDpAsState(targetValue = if (on) 22.dp else 2.dp, label = "switchKnob")
    Box(
        modifier
            .size(width = 46.dp, height = 26.dp)
            .clip(AppShapes.pill)
            .background(trackColor)
            .clickable(onClick = onToggle)
            .semantics { contentDescription = label },
    ) {
        Box(
            Modifier
                .offset(x = knobOffset, y = 2.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(AppColors.textPrimary),
        )
    }
}

/** 2x2 grid of reminder cadences; the selected chip is highlighted. */
@Composable
fun FrequencyGrid(
    options: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(2).forEach { rowOptions ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowOptions.forEach { minutes ->
                    FrequencyChip(
                        minutes = minutes,
                        active = minutes == selected,
                        onClick = { onSelect(minutes) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun FrequencyChip(
    minutes: Int,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = frequencyLabel(minutes)
    Box(
        modifier
            .clip(AppShapes.pill)
            .then(
                if (active) {
                    Modifier.background(AppColors.glassActiveFill, AppShapes.pill)
                } else {
                    Modifier.glass(AppShapes.pill)
                },
            )
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = AppDimens.touchTargetMin)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = AppType.segUnit,
            color = if (active) AppColors.on else AppColors.textSecondary,
        )
    }
}

/** Labelled −/value/+ stepper row for an hour boundary. */
@Composable
fun HourStepper(
    label: String,
    valueText: String,
    onEarlier: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .glass(AppShapes.quote)
            .padding(horizontal = AppDimens.quotePadding, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = AppType.bannerBody, color = AppColors.textSecondary, modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StepButton(symbol = "−", contentDescription = stringResource(R.string.settings_earlier), onClick = onEarlier)
            Text(
                text = valueText,
                style = AppType.segUnit,
                color = AppColors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(88.dp),
            )
            StepButton(symbol = "+", contentDescription = stringResource(R.string.settings_later), onClick = onLater)
        }
    }
}

@Composable
private fun StepButton(symbol: String, contentDescription: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(32.dp)
            .clip(CircleShape)
            .glass(CircleShape)
            .clickable(onClick = onClick)
            .clearAndSetSemantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Text(symbol, style = AppType.segValue, color = AppColors.textPrimary)
    }
}

/** Solid primary "Done" button. */
@Composable
fun DoneButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(AppDimens.touchTargetMin)
            .clip(AppShapes.bannerButton)
            .background(AppColors.textPrimary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.settings_done),
            style = AppType.bannerButton,
            color = AppColors.onDeep,
        )
    }
}

/** "30 min" / "1 hour" / "90 min" / "2 hours" for a cadence in minutes. */
@Composable
fun frequencyLabel(minutes: Int): String =
    if (minutes % SettingsConstants.MINUTES_PER_HOUR == 0) {
        val hours = minutes / SettingsConstants.MINUTES_PER_HOUR
        pluralStringResource(R.plurals.settings_freq_hours, hours, hours)
    } else {
        stringResource(R.string.settings_freq_minutes, minutes)
    }
