package com.translator.uzbek.english.dictionary.presentation.statistics

import com.translator.uzbek.english.dictionary.data.database.dao.WordDao
import com.translator.uzbek.english.dictionary.data.database.model.WordModel
import com.translator.uzbek.english.dictionary.data.datastore.DictionaryStore
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StatisticsViewModel : ViewModel(), KoinComponent {

    private val wordDao by inject<WordDao>()
    private val dictionaryStore by inject<DictionaryStore>()

    private val stateData = MutableStateFlow(StatisticsState())
    val state = stateData.asStateFlow()

    init {
        fetchStatisticsStore()
    }

    fun onEvent(event: StatisticsEvent) {
    }

    private fun fetchStatisticsStore() {
        viewModelScope.launch {
            combine(
                wordDao.getCountStatusWords(WordModel.WordStatus.Learning),
                wordDao.getCountStatusWords(WordModel.WordStatus.Learned)
            ) { allLearning, allLearned ->
                stateData.update {
                    it.copy(
                        dailyGoal = dictionaryStore.getDailyGoal(),
                        today = 3,
                        currentStreak = 12,
                        learned = 8,
                        learning = 35,
                        new = 85,
                        skipped = 24,
                        allLearning = allLearning,
                        allLearned = allLearned,
                        allBestStreak = 132,
                        startOfLearning = dictionaryStore.getStartOfLearning()
                    )
                }
            }.collect()
        }
    }
}