package com.translator.uzbek.english.dictionary.presentation.settings

import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.MutableStateFlow
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.translator.uzbek.english.dictionary.core.datetime.TimeModel
import com.translator.uzbek.english.dictionary.data.datastore.AppStore
import com.translator.uzbek.english.dictionary.data.datastore.DictionaryStore
import com.translator.uzbek.english.dictionary.data.model.mode.FirstLanguageMode
import com.translator.uzbek.english.dictionary.data.model.mode.LanguageMode
import com.translator.uzbek.english.dictionary.data.model.mode.ThemeMode
import com.translator.uzbek.english.dictionary.shared.Event
import com.translator.uzbek.english.dictionary.shared.EventChannel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel : KMMViewModel(), KoinComponent {

    private val appStore by inject<AppStore>()
    private val dictionaryStore by inject<DictionaryStore>()

    private val stateData = MutableStateFlow(viewModelScope, SettingsState())

    @NativeCoroutinesState
    val state = stateData.asStateFlow()

    init {
        fetchSettingsStore()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.SetDailyGoal -> setDailyGoal(event.goal)
            is SettingsEvent.SetNewWordFirstLanguage -> setNewWordFirstLanguage(event.firstLanguage)
            is SettingsEvent.SetRepeatedFirstLanguage -> setRepeatedFirstLanguage(event.firstLanguage)
            is SettingsEvent.ShowTranscription -> showTranscription(event.show)
            is SettingsEvent.SetAppLanguage -> setAppLanguage(event.language)
            is SettingsEvent.SetThemeMode -> setThemeMode(event.themeMode)
            is SettingsEvent.CheckReminder -> checkReminder(event.checked)
            is SettingsEvent.SetReminder -> setReminder(event)
            is SettingsEvent.CheckSoundEffects -> checkSoundEffects(event.checked)
            is SettingsEvent.CheckAutoPronounce -> checkAutoPronounce(event.checked)
        }
    }

    private fun fetchSettingsStore() {
        stateData.update {
            it.copy(
                dailyGoal = dictionaryStore.getDailyGoal(),
                newWordFirstLanguage = dictionaryStore.getNewWordFirstLanguage(),
                repeatedFirstLanguage = dictionaryStore.getRepeatedFirstLanguage(),
                showTranscription = dictionaryStore.showTranscription(),
                appLanguage = appStore.getAppLanguage(),
                themeMode = appStore.getThemeMode(),
                isReminderEnabled = dictionaryStore.isReminderEnabled(),
                reminderDays = dictionaryStore.getReminderDays(),
                reminderTime = dictionaryStore.getReminderTime(),
                isSoundEffectsEnabled = dictionaryStore.isSoundEffectsEnabled(),
                isAutoPronounceEnabled = dictionaryStore.isAutoPronounceEnabled(),
            )
        }
    }

    private fun setDailyGoal(goal: Int) {
        dictionaryStore.setDailyGoal(goal)

        stateData.update { it.copy(dailyGoal = goal) }
    }

    private fun setNewWordFirstLanguage(firstLanguage: FirstLanguageMode) {
        dictionaryStore.setNewWordFirstLanguage(firstLanguage)

        stateData.update { it.copy(newWordFirstLanguage = firstLanguage) }
    }

    private fun setRepeatedFirstLanguage(firstLanguage: FirstLanguageMode) {
        dictionaryStore.setRepeatedFirstLanguage(firstLanguage)

        stateData.update { it.copy(repeatedFirstLanguage = firstLanguage) }
    }

    private fun showTranscription(show: Boolean) {
        dictionaryStore.setShowTranscription(show)

        stateData.update { it.copy(showTranscription = show) }
    }

    private fun setAppLanguage(language: LanguageMode) {
        appStore.setAppLanguage(language)

        stateData.update { it.copy(appLanguage = language) }

        EventChannel.sendEvent(Event.LanguageChanged(language))
    }

    private fun setThemeMode(themeMode: ThemeMode) {
        appStore.setThemeMode(themeMode)

        stateData.update { it.copy(themeMode = themeMode) }

        EventChannel.sendEvent(Event.ThemeModeChanged(themeMode))
    }

    private fun checkReminder(checked: Boolean) {
        dictionaryStore.setReminderEnabled(checked)

        stateData.update { it.copy(isReminderEnabled = checked) }
    }

    private fun setReminder(event: SettingsEvent.SetReminder) {
        val time = TimeModel(event.hour, event.minute)

        dictionaryStore.setReminderTime(time)
        dictionaryStore.setReminderWeekdays(event.weekdays)

        stateData.update {
            it.copy(
                reminderDays = dictionaryStore.getReminderDays(),
                reminderTime = time,
            )
        }
    }

    private fun checkSoundEffects(checked: Boolean) {
        dictionaryStore.setSoundEffectsEnabled(checked)

        stateData.update { it.copy(isSoundEffectsEnabled = checked) }
    }

    private fun checkAutoPronounce(checked: Boolean) {
        dictionaryStore.setAutoPronounceEnabled(checked)

        stateData.update { it.copy(isAutoPronounceEnabled = checked) }
    }
}