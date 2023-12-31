package com.translator.uzbek.english.dictionary.presentation.addWord

import com.translator.uzbek.english.dictionary.data.database.dao.WordDao
import com.translator.uzbek.english.dictionary.data.database.model.WordModel
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddWordViewModel : ViewModel(), KoinComponent {

    private val wordDao by inject<WordDao>()

    private val stateData = MutableStateFlow(AddWordState())
    val state = stateData.asStateFlow()

    private val wordId = MutableStateFlow("")
    private val dictionaryId = MutableStateFlow("")

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
        this.wordId.value = wordId
        this.dictionaryId.value = dictionaryId

        viewModelScope.launch {
            wordDao.fetchWordById(wordId).collectLatest { model ->
                if (model != null) {
                    stateData.update {
                        it.copy(
                            word = model.word,
                            translation = model.translation,
                            transcription = model.transcription.orEmpty(),
                            repeats = model.repeats,
                            isNewWord = false
                        )
                    }
                } else {
                    stateData.update {
                        it.copy(isNewWord = true)
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

        wordDao.insertWord(
            id = wordId.value,
            dictionaryId = dictionaryId.value,
            word = state.value.word,
            translation = state.value.translation,
            transcription = state.value.transcription,
            repeats = state.value.repeats,
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