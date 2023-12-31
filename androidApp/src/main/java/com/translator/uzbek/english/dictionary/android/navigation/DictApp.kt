package com.translator.uzbek.english.dictionary.android.navigation

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.translator.uzbek.english.dictionary.android.design.theme.DictTheme
import com.translator.uzbek.english.dictionary.android.presentation.NavGraphs
import com.translator.uzbek.english.dictionary.android.presentation.destinations.AddDictionaryScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.AddWordScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.AppLanguageScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.DailyGoalScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.Destination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.DictionaryScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.DictionarySelectionScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.DictionaryWordsScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.FeedbackScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.LearnScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.LearnWordsScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.PremiumScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.ReminderScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.RepeatWordsScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.SearchForWordsScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.SettingsScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.StatisticsScreenDestination
import com.translator.uzbek.english.dictionary.android.presentation.destinations.ThemeModeScreenDestination
import com.translator.uzbek.english.dictionary.data.model.mode.LanguageMode
import com.translator.uzbek.english.dictionary.data.model.mode.ThemeMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DictApp(
    hasSubscription: Boolean,
    language: LanguageMode,
    themeMode: ThemeMode
) {
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()

    DictTheme(
        hasSubscription = hasSubscription,
        language = language,
        themeMode = themeMode
    ) {
        DictScaffold(
            navController = navController,
            startRoute = LearnScreenDestination,
            bottomBar = { destination ->
                QRBottomBar(
                    show = destination.shouldShowBottomBar,
                    showAds = destination.shouldShowAds,
                    hasSubscription = hasSubscription,
                    navController = navController
                )
            }
        ) { padding ->
            DestinationsNavHost(
                engine = engine,
                navController = navController,
                navGraph = NavGraphs.root,
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding),
                startRoute = LearnScreenDestination
            )
        }
    }
}

private val Destination.shouldShowBottomBar
    get() = when (this) {
        LearnScreenDestination,
        DictionaryScreenDestination,
        StatisticsScreenDestination,
        SettingsScreenDestination,
        FeedbackScreenDestination,
        AppLanguageScreenDestination,
        ThemeModeScreenDestination,
        DailyGoalScreenDestination,
        ReminderScreenDestination -> true

        AddDictionaryScreenDestination,
        SearchForWordsScreenDestination,
        DictionaryWordsScreenDestination,
        AddWordScreenDestination,
        DictionarySelectionScreenDestination,
        LearnWordsScreenDestination,
        RepeatWordsScreenDestination,
        PremiumScreenDestination -> false
    }

private val Destination.shouldShowAds
    get() = when (this) {
        PremiumScreenDestination -> false

        else -> true
    }