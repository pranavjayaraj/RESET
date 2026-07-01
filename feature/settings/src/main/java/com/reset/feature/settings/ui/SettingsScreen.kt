package com.reset.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.reset.core.designsystem.AppColors
import com.reset.core.designsystem.AppType
import com.reset.model.domain.model.HomePreferences
import com.reset.feature.settings.R
import com.reset.feature.settings.SettingsConstants
import com.reset.feature.settings.SettingsState
import com.reset.feature.settings.navigation.SettingsIntent
import com.reset.feature.settings.ui.components.DoneButton
import com.reset.feature.settings.ui.components.FrequencyGrid
import com.reset.feature.settings.ui.components.HourStepper
import com.reset.feature.settings.ui.components.ReminderMasterCard
import com.reset.feature.settings.ui.components.SectionHeader
import com.reset.feature.settings.ui.components.SettingsTopBar
import com.reset.feature.settings.ui.components.frequencyLabel

@Composable
fun SettingsScreen(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        SettingsTopBar(onBack = { onIntent(SettingsIntent.HandleBackPress) })

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            SectionHeader(stringResource(R.string.settings_section_reminders))

            ReminderMasterCard(
                title = stringResource(R.string.settings_master_title),
                summary = reminderSummary(state),
                enabled = state.remindersEnabled,
                onToggle = { onIntent(SettingsIntent.ToggleReminders) },
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .alpha(if (state.remindersEnabled) 1f else SettingsConstants.disabledAlpha),
                verticalArrangement = Arrangement.spacedBy(SettingsConstants.rowSpacing),
            ) {
                SectionHeader(stringResource(R.string.settings_section_frequency))
                FrequencyGrid(
                    options = HomePreferences.REMINDER_EVERY_OPTIONS,
                    selected = state.everyMin,
                    onSelect = { onIntent(SettingsIntent.SelectEveryMin(it)) },
                )

                SectionHeader(stringResource(R.string.settings_section_hours))
                HourStepper(
                    label = stringResource(R.string.settings_from),
                    valueText = SettingsConstants.formatHour(state.startHour),
                    onEarlier = { onIntent(SettingsIntent.AdjustStartHour(-1)) },
                    onLater = { onIntent(SettingsIntent.AdjustStartHour(1)) },
                )
                HourStepper(
                    label = stringResource(R.string.settings_until),
                    valueText = SettingsConstants.formatHour(state.endHour),
                    onEarlier = { onIntent(SettingsIntent.AdjustEndHour(-1)) },
                    onLater = { onIntent(SettingsIntent.AdjustEndHour(1)) },
                )

                Text(
                    text = stringResource(R.string.settings_note),
                    style = AppType.bubbleSub,
                    color = AppColors.textFaint,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        DoneButton(
            onClick = { onIntent(SettingsIntent.HandleBackPress) },
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun reminderSummary(state: SettingsState): String =
    if (state.remindersEnabled) {
        stringResource(
            R.string.settings_summary_on,
            frequencyLabel(state.everyMin),
            SettingsConstants.formatHour(state.startHour),
            SettingsConstants.formatHour(state.endHour),
        )
    } else {
        stringResource(R.string.settings_summary_off)
    }
