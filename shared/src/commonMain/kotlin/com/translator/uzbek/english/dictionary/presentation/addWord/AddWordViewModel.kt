package com.translator.uzbek.english.dictionary.presentation.addWord

import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.MutableStateFlow
import com.rickclephas.kmm.viewmodel.coroutineScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.translator.uzbek.english.dictionary.data.database.dao.WordDao
import com.translator.uzbek.english.dictionary.data.database.model.WordModel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddWordViewModel : KMMViewModel(), KoinComponent {

    private val wordDao by inject<WordDao>()

    private val stateData = MutableStateFlow(viewModelScope, AddWordState())

    @NativeCoroutinesState
    val state = stateData.asStateFlow()

    fun onEvent(event: AddWordEvent) {
        when (event) {
            is AddWordEvent.FetchWord -> fetchWord(event.wordId, event.dictionaryId)
            is AddWordEvent.ChangeWord -> changeWord(event.word)
            is AddWordEvent.ChangeTranslation -> changeTranslation(event.translation)
            is AddWordEvent.ChangeTranscription -> changeTranscription(event.transcription)

            AddWordEvent.Insert -> insert()
        }
    }

    private fun fetchWord(wordId: String, dictionaryId: String) {
        viewModelScope.coroutineScope.launch {
            wordDao.fetchWordById(wordId).collectLatest { model ->
                if (model != null) {
                    stateData.update {
                        it.copy(
                            wordId = wordId,
                            dictionaryId = dictionaryId,
                            word = model.word,
                            translation = model.translation,
                            transcription = model.transcription.orEmpty(),
                            isNewWord = false
                        )
                    }
                } else {
                    stateData.update {
                        it.copy(
                            wordId = wordId,
                            dictionaryId = dictionaryId,
                            isNewWord = true
                        )
                    }
                }
            }
        }
    }

    private fun changeWord(word: String) {
        stateData.update {
            it.copy(
                word = word,
                isEnabled = word.isNotBlank() && it.translation.isNotBlank(),
            )
        }
    }

    private fun changeTranslation(translation: String) {
        stateData.update {
            it.copy(
                translation = translation,
                isEnabled = translation.isNotBlank() && it.word.isNotBlank(),
            )
        }
    }

    private fun changeTranscription(transcription: String) {
        stateData.update { it.copy(transcription = transcription) }
    }

    private fun insert() {
        setLoading()

        wordDao.insert(
            id = state.value.wordId,
            dictionaryId = state.value.dictionaryId,
            word = state.value.word,
            translation = state.value.translation,
            transcription = state.value.transcription,
            status = WordModel.WordStatus.New
        )

        setSuccess()
    }

    private fun setLoading() {
        stateData.update { it.copy(isLoading = true) }
    }

    private fun setSuccess() {
        stateData.update {
            it.copy(
                isLoading = false,
                isSuccess = true,
            )
        }
    }
}