package com.pkapps.app.screen.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pkapps.app.common.updateAs
import com.pkapps.ui.domain.exception.NetworkException
import com.pkapps.ui.domain.model.Country
import com.pkapps.ui.domain.model.QuizQuestion
import com.pkapps.ui.domain.usecase.GenerateQuizResultsUseCase
import com.pkapps.ui.domain.usecase.GenerateQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val generateQuiz: GenerateQuizUseCase,
    private val generateResults: GenerateQuizResultsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            if (uiState.value is QuizUiState.NetworkError) {
                _uiState.update { QuizUiState.Loading }
            }

            _uiState.update {
                try {
                    val quiz = generateQuiz()
                    QuizUiState.QuizPhase(
                        quiz = quiz,
                        progress = 1.0 / quiz.questions.size
                    )
                } catch (_: NetworkException) {
                    QuizUiState.NetworkError
                }
            }
        }
    }

    fun pickAnswer(question: QuizQuestion, answer: Country) {
        _uiState.updateAs { state: QuizUiState.QuizPhase ->
            state.copy(
                pickedAnswers = buildMap {
                    putAll(state.pickedAnswers)
                    put(question, answer)
                }
            )
        }
    }

    fun onNext() {
        viewModelScope.launch {
            _uiState.updateAs { state: QuizUiState.QuizPhase ->
                if (state.currentQuestion < state.quiz.questions.count() - 1) {
                    state.copy(
                        currentQuestion = state.currentQuestion + 1,
                        progress = (state.currentQuestion + 2).toDouble() / state.quiz.questions.size
                    )
                } else {
                    QuizUiState.ResultsPhase(generateResults(state.quiz, state.pickedAnswers))
                }
            }
        }
    }
}