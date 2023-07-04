package com.pkapps.ui.domain.model

import com.pkapps.ui.domain.dict.QuestionDifficulty

data class QuizQuestion(
    val difficulty: QuestionDifficulty,
    val correctAnswer: Country,
    val options: List<Country>
)
