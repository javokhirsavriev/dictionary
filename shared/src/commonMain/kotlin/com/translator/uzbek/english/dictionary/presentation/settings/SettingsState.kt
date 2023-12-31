package com.translator.uzbek.english.dictionary.presentation.settings

import com.translator.uzbek.english.dictionary.core.datetime.TimeModel
import com.translator.uzbek.english.dictionary.core.helpers.Constants
import com.translator.uzbek.english.dictionary.data.model.mode.LanguageMode
import com.translator.uzbek.english.dictionary.data.model.mode.ThemeMode

data class SettingsState(
    val dailyGoal: Int = Constants.defaultDailyGoal,
    val showTranscription: Boolean = true,
    val appLanguage: LanguageMode = LanguageMode.Uzbek,
    val themeMode: ThemeMode = ThemeMode.System,
    val isReminderEnabled: Boolean = true,
    val reminderDays: List<Boolean> = listOf(true, true, true, true, true, true, true),
    val reminderTime: TimeModel = TimeModel(
        hour = Constants.defaultReminderHour,
        minute = Constants.defaultReminderMinute
    ),
    val isSoundEffectsEnabled: Boolean = true,
    val isAutoPronounceEnabled: Boolean = true,
)