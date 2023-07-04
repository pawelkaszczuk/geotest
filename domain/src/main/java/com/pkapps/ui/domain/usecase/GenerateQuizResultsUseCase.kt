package com.pkapps.ui.domain.usecase

import com.pkapps.ui.domain.model.Country
import com.pkapps.ui.domain.model.Quiz
import com.pkapps.ui.domain.model.QuizQuestion
import com.pkapps.ui.domain.model.QuizResults
import javax.inject.Inject

class GenerateQuizResultsUseCase @Inject constructor() {

    operator fun invoke(quiz: Quiz, answers: Map<QuizQuestion, Country>): QuizResults {

        val stats = quiz.questions.map { question ->
            when (answers[question]) {
                question.correctAnswer -> AnswerStatus.CORRECT
                else -> AnswerStatus.WRONG
            }
        }

        return QuizResults(
            correctAnswers = stats.count { it == AnswerStatus.CORRECT },
            wrongAnswers = stats.count { it == AnswerStatus.WRONG },
            correctRatio = if (stats.isNotEmpty()) stats.count { it == AnswerStatus.CORRECT } / stats.size.toDouble() else 0.0
        )
    }

    private enum class AnswerStatus { CORRECT, WRONG, SKIPPED }
}