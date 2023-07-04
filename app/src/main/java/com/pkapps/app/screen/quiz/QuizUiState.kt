package com.pkapps.app.screen.quiz

import com.pkapps.ui.domain.model.Country
import com.pkapps.ui.domain.model.QuizQuestion
import com.pkapps.ui.domain.model.QuizResults
import com.pkapps.ui.domain.model.Quiz

sealed class QuizUiState {
    object Loading : QuizUiState()
    object NetworkError : QuizUiState()
    data class QuizPhase(
        val quiz: Quiz,
        val progress: Double,
        val currentQuestion: Int = 0,
        val pickedAnswers: Map<QuizQuestion, Country> = emptyMap(),
    ) : QuizUiState()

    data class ResultsPhase(
        val results: QuizResults
    ) : QuizUiState()
}