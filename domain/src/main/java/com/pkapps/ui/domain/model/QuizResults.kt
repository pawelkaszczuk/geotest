package com.pkapps.ui.domain.model

data class QuizResults(
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val correctRatio: Double
)
