package com.translator.uzbek.english.dictionary.android.presentation.settings

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.spec.Direction
import com.translator.uzbek.english.dictionary.android.R
import com.translator.uzbek.english.dictionary.android.core.extensions.clickableSingle
import com.translator.uzbek.english.dictionary.android.core.extensions.defaultPadding
import com.translator.uzbek.english.dictionary.android.core.extensions.openUrl
import com.translator.uzbek.english.dictionary.android.core.extensions.shareText
import com.translator.uzbek.english.dictionary.android.design.components.ConfirmationDialog
import com.translator.uzbek.english.dictionary.android.design.components.DictContainer
import com.translator.uzbek.english.dictionary.android.design.components.DictIcon
import com.translator.uzbek.english.dictionary.android.design.localization.LocalStrings
import com.translator.uzbek.english.dictionary.android.design.localization.StringResources
import com.translator.uzbek.english.dictionary.android.design.mapper.localized
import com.translator.uzbek.english.dictionary.android.design.mapper.weekdays
import com.translator.uzbek.english.dictionary.android.design.theme.DividerColor
import com.translator.uzbek.english.dictionary.android.design.theme.LocalSubscription
import com.translator.uzbek.english.dictionary.android.design.theme.WindowBackground
import com.translator.uzbek.english.dictionary.android.navigation.ReminderResult
import com.translator.uzbek.english.dictionary.android.presentation.destinations.AppLanguageScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.DailyGoalScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.FeedbackScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.PremiumScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.ReminderScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.ThemeModeScreenDestination
import com.translator.uzbek.english.dictionary.data.model.mode.LanguageMode
import com.translator.uzbek.english.dictionary.data.model.mode.ThemeMode
import com.translator.uzbek.english.dictionary.presentation.settings.SettingsEvent
import com.translator.uzbek.english.dictionary.presentation.settings.SettingsState
import com.translator.uzbek.english.dictionary.presentation.settings.SettingsViewModel
import com.translator.uzbek.english.dictionary.shared.appUrl
import com.translator.uzbek.english.dictionary.shared.appVersion
import com.translator.uzbek.english.dictionary.shared.developerUrl

@Destination
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    navigator: DestinationsNavigator,
    resultDailyGoal: ResultRecipient<DailyGoalScreenDestination, Int>,
    resultAppLanguage: ResultRecipient<AppLanguageScreenDestination, LanguageMode>,
    resultThemeMode: ResultRecipient<ThemeModeScreenDestination, ThemeMode>,
    resultReminder: ResultRecipient<ReminderScreenDestination, ReminderResult>,
) {
    val context = LocalContext.current
    val strings = LocalStrings.current
    val hasSubscription = LocalSubscription.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }
    val reminderWeekdays = remember(state.reminderDays) {
        state.reminderDays.weekdays(strings)
    }

    resultDailyGoal.onNavResult { result ->
        if (result is NavResult.Value) {
            viewModel.onEvent(SettingsEvent.SetDailyGoal(result.value))
        }
    }
    resultAppLanguage.onNavResult { result ->
        if (result is NavResult.Value) {
            viewModel.onEvent(SettingsEvent.SetAppLanguage(result.value))
        }
    }
    resultThemeMode.onNavResult { result ->
        if (result is NavResult.Value) {
            viewModel.onEvent(SettingsEvent.SetThemeMode(result.value))
        }
    }
    resultReminder.onNavResult { result ->
        if (result is NavResult.Value) {
            viewModel.onEvent(
                SettingsEvent.SetReminder(
                    hour = result.value.hour,
                    minute = result.value.minute,
                    weekdays = result.value.weekdays
                )
            )
        }
    }

    if (showResetDialog) {
        ConfirmationDialog(
            message = strings.confirmationResetAllProgress,
            onConfirm = {
                viewModel.onEvent(SettingsEvent.ResetProgress)
                showResetDialog = false
            },
            onDismiss = {
                showResetDialog = false
            }
        )
    }

    DictContainer(strings.settings) {
        SettingsScreenContent(
            context = context,
            strings = strings,
            state = state,
            hasSubscription = hasSubscription,
            reminderWeekdays = reminderWeekdays,
            onEvent = viewModel::onEvent,
            onNavigate = navigator::navigate,
            onReset = {
                showResetDialog = true
            }
        )
    }
}

@Composable
private fun SettingsScreenContent(
    context: Context,
    strings: StringResources,
    state: SettingsState,
    hasSubscription: Boolean,
    reminderWeekdays: String,
    onEvent: (SettingsEvent) -> Unit,
    onNavigate: (Direction) -> Unit,
    onReset: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WindowBackground),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { LearningContent(strings, state, onEvent, onNavigate) }
        item { PreferencesContent(strings, state, onEvent, reminderWeekdays, onNavigate) }
        item { ProgressContent(strings, hasSubscription, onReset, onNavigate) }
        item { GeneralContent(context, strings, onNavigate) }
        item {
            Text(
                text = "${strings.appVersion} $appVersion",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LearningContent(
    strings: StringResources,
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    onNavigate: (Direction) -> Unit
) {
    HeaderContent(strings.learning) {
        NavigateContent(
            title = strings.dailyGoal,
            value = if (state.dailyGoal == 0) {
                strings.notSet
            } else {
                strings.countNewWords(state.dailyGoal)
            }
        ) {
            onNavigate(DailyGoalScreenDestination(state.dailyGoal))
        }

        DividerContent()

        SwitchContent(
            title = strings.showTranscription,
            checked = state.showTranscription
        ) {
            onEvent(SettingsEvent.ShowTranscription(it))
        }
    }
}

@Composable
private fun PreferencesContent(
    strings: StringResources,
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    reminderWeekdays: String,
    onNavigate: (Direction) -> Unit
) {
    HeaderContent(strings.preferences) {
        NavigateContent(
            title = strings.appLanguage,
            value = state.appLanguage.language
        ) {
            onNavigate(AppLanguageScreenDestination(state.appLanguage))
        }

        DividerContent()

        NavigateContent(
            title = strings.theme,
            value = state.themeMode.localized()
        ) {
            onNavigate(ThemeModeScreenDestination(state.themeMode))
        }

        DividerContent()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickableSingle(
                    enabled = state.isReminderEnabled,
                    onClick = {
                        onNavigate(ReminderScreenDestination)
                    }
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = strings.reminder,
                    style = MaterialTheme.typography.bodyLarge,
                )

                if (state.isReminderEnabled) {
                    Text(
                        text = "${reminderWeekdays}, ${state.reminderTime}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Switch(
                checked = state.isReminderEnabled,
                onCheckedChange = {
                    onEvent(SettingsEvent.CheckReminder(it))
                },
                modifier = Modifier
                    .width(39.dp)
                    .height(24.dp)
                    .scale(.85f)
            )
        }

        DividerContent()

        SwitchContent(
            title = strings.soundEffects,
            checked = state.isSoundEffectsEnabled
        ) {
            onEvent(SettingsEvent.CheckSoundEffects(it))
        }

        DividerContent()

        SwitchContent(
            title = strings.autoPronounce,
            checked = state.isAutoPronounceEnabled
        ) {
            onEvent(SettingsEvent.CheckAutoPronounce(it))
        }
    }
}

@Composable
private fun ProgressContent(
    strings: StringResources,
    hasSubscription: Boolean,
    onReset: () -> Unit,
    onNavigate: (Direction) -> Unit
) {
    HeaderContent(strings.progress) {
        NavigateContent(
            title = strings.createBackup,
            hasSubscription = hasSubscription
        ) {
            if (hasSubscription) {
            } else {
                onNavigate(PremiumScreenDestination)
            }
        }

        DividerContent()

        NavigateContent(
            title = strings.restoreData,
            hasSubscription = hasSubscription
        ) {
            if (hasSubscription) {
            } else {
                onNavigate(PremiumScreenDestination)
            }
        }

        DividerContent()

        NavigateContent(
            title = strings.resetProgress,
            textColor = MaterialTheme.colorScheme.error,
            iconColor = MaterialTheme.colorScheme.error,
            onClick = onReset
        )
    }
}

@Composable
private fun GeneralContent(
    context: Context,
    strings: StringResources,
    onNavigate: (Direction) -> Unit
) {
    HeaderContent(strings.general) {
        NavigateContent(strings.otherApps) {
            context.openUrl(developerUrl)
        }

        DividerContent()

        NavigateContent(strings.feedback) {
            onNavigate(FeedbackScreenDestination)
        }

        DividerContent()

        NavigateContent(strings.share) {
            context.shareText(strings.shareDescription)
        }

        DividerContent()

        NavigateContent(strings.rateUs) {
            context.openUrl(appUrl)
        }
    }
}

@Composable
private fun HeaderContent(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )

        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .border(
                    width = 1.dp,
                    color = DividerColor,
                    shape = MaterialTheme.shapes.medium
                )
                .background(MaterialTheme.colorScheme.background)
        ) {
            content()
        }
    }
}

@Composable
private fun NavigateContent(
    title: String,
    value: String = "",
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    iconColor: Color = MaterialTheme.colorScheme.outline,
    hasSubscription: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSingle(onClick = onClick)
            .defaultPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            modifier = Modifier.weight(1f)
        )

        if (!hasSubscription) {
            Image(
                painter = painterResource(id = R.drawable.ic_subscription),
                contentDescription = null
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = iconColor
                )
            }

            DictIcon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                color = iconColor
            )
        }
    }
}

@Composable
private fun SwitchContent(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .width(39.dp)
                .height(24.dp)
                .scale(.85f)
        )
    }
}

@Composable
fun DividerContent() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 16.dp),
        color = DividerColor
    )
}