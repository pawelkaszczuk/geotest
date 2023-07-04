package com.pkapps.ui.domain.usecase

import com.pkapps.ui.domain.dict.QuestionDifficulty
import com.pkapps.ui.domain.model.Country
import com.pkapps.ui.domain.model.Quiz
import com.pkapps.ui.domain.model.QuizQuestion
import com.pkapps.ui.domain.model.QuizResults
import domain.Dummy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GenerateQuizResultsUseCaseTest {

    val useCase = GenerateQuizResultsUseCase()

    @Test
    fun `WHEN user answers the quiz THEN return the result`() {
        val quiz = Quiz(
            listOf(
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("A"), emptyList()),
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("B"), emptyList()),
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("C"), emptyList()),
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("D"), emptyList()),
            ))

        val answers = mapOf(
            quiz.questions[0] to Dummy.country.copy("A"),
            quiz.questions[1] to Dummy.country.copy("B"),
            quiz.questions[2] to Dummy.country.copy("B"),
            quiz.questions[3] to Dummy.country.copy("D"),
        )

        val expected = QuizResults(
            correctAnswers = 3,
            wrongAnswers = 1,
            correctRatio = .75
        )

        assertEquals(expected, useCase(quiz, answers))
    }

    @Test
    fun `WHEN all answers are correct THEN return all correct results`() {
        val quiz = Quiz(
            listOf(
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("A"), emptyList()),
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("B"), emptyList()),
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("C"), emptyList()),
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("D"), emptyList()),
            ))

        val answers = mapOf(
            quiz.questions[0] to Dummy.country.copy("A"),
            quiz.questions[1] to Dummy.country.copy("B"),
            quiz.questions[2] to Dummy.country.copy("C"),
            quiz.questions[3] to Dummy.country.copy("D"),
        )

        val expected = QuizResults(
            correctAnswers = 4,
            wrongAnswers = 0,
            correctRatio = 1.0
        )

        assertEquals(expected, useCase(quiz, answers));
    }

    @Test
    fun `WHEN all answers are wrong THEN return all wrong results`() {
        val quiz = Quiz(
            listOf(
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("A"), emptyList()),
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("B"), emptyList()),
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("C"), emptyList()),
            ))

        val answers = mapOf(
            quiz.questions[0] to Dummy.country.copy("E"),
            quiz.questions[1] to Dummy.country.copy("A"),
            quiz.questions[2] to Dummy.country.copy("3"),
        )

        val expected = QuizResults(
            correctAnswers = 0,
            wrongAnswers = 3,
            correctRatio = 0.0
        )

        assertEquals(expected, useCase(quiz, answers));
    }

    @Test
    fun `WHEN the quiz is empty THEN return a dummy response`() {
        val quiz = Quiz(emptyList())
        val answers = emptyMap<QuizQuestion, Country>()

        val expected = QuizResults(
            correctAnswers = 0,
            wrongAnswers = 0,
            correctRatio = 0.0
        )

        assertEquals(expected, useCase(quiz, answers));
    }

    @Test
    fun `WHEN user somehow skipped some questions THEN treat them as wrong answers`() {
        val quiz = Quiz(
            listOf(
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("A"), emptyList()),
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("B"), emptyList()),
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("C"), emptyList()),
                QuizQuestion(QuestionDifficulty.EASY, Dummy.country.copy("D"), emptyList()),
            ))

        val answers = mapOf(
            quiz.questions[0] to Dummy.country.copy("A"),
            quiz.questions[1] to Dummy.country.copy("A"),
        )

        val expected = QuizResults(
            correctAnswers = 1,
            wrongAnswers = 3,
            correctRatio = .25
        )

        assertEquals(expected, useCase(quiz, answers));
    }
}