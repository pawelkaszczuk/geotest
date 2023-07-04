package com.pkapps.app.screen.quiz

import StandardTestDispatcherExtension
import com.pkapps.ui.domain.dict.QuestionDifficulty
import com.pkapps.ui.domain.exception.NetworkException
import com.pkapps.ui.domain.model.Quiz
import com.pkapps.ui.domain.model.QuizQuestion
import com.pkapps.ui.domain.model.QuizResults
import com.pkapps.ui.domain.usecase.GenerateQuizResultsUseCase
import com.pkapps.ui.domain.usecase.GenerateQuizUseCase
import domain.Dummy
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(MockKExtension::class, StandardTestDispatcherExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
internal class QuizViewModelTest {

    @MockK
    private lateinit var generateQuizUseCase: GenerateQuizUseCase

    @MockK
    private lateinit var generateQuizResultsUseCase: GenerateQuizResultsUseCase

    private lateinit var vm: QuizViewModel
    private val state: QuizUiState
        get() = vm.uiState.value


    @BeforeEach
    fun before() {
        coEvery { generateQuizUseCase(any(), any(), any()) } returns dummyQuiz

        vm = QuizViewModel(generateQuizUseCase, generateQuizResultsUseCase)
    }


    @Test
    fun `WHEN vm is initialized THEN show the loading and the quiz phase`() = runTest {
        vm = QuizViewModel(generateQuizUseCase, generateQuizResultsUseCase)
        assertEquals(QuizUiState.Loading, state)
        advanceUntilIdle()
        assertEquals(
            QuizUiState.QuizPhase(
                quiz = dummyQuiz,
                progress = .5,
                currentQuestion = 0,
                pickedAnswers = emptyMap()
            ),
            state
        )
    }

    @Test
    fun `WHEN there's a network error THEN show an error and allow to reload`() = runTest {
        coEvery { generateQuizUseCase(any(), any(), any()) } throws NetworkException()
        vm = QuizViewModel(generateQuizUseCase, generateQuizResultsUseCase)
        assertEquals(QuizUiState.Loading, state)
        advanceUntilIdle()
        assertEquals(QuizUiState.NetworkError, state)

        coEvery { generateQuizUseCase(any(), any(), any()) } coAnswers {
            delay(1_000)
            dummyQuiz
        }
        vm.load()
        runCurrent()
        assertEquals(QuizUiState.Loading, state)
        advanceUntilIdle()
        assertEquals(
            QuizUiState.QuizPhase(
                quiz = dummyQuiz,
                progress = .5,
                currentQuestion = 0,
                pickedAnswers = emptyMap()
            ),
            state
        )
    }

    @Test
    fun `WHEN user pick an answer THEN register the choice`() = runTest {
        vm.pickAnswer(dummyQuiz.questions[0], Dummy.country.copy(name = "TB"))

        assertEquals(
            mapOf(dummyQuiz.questions[0] to Dummy.country.copy(name = "TB")),
            (state as? QuizUiState.QuizPhase)?.pickedAnswers
        )
    }

    @Test
    fun `WHEN user fills the quiz THEN show the results`() = runTest {
        coEvery { generateQuizResultsUseCase(any(), any()) } returns QuizResults(1, 1, .5)
        vm.pickAnswer(dummyQuiz.questions[0], Dummy.country.copy(name = "TB"))
        vm.onNext()
        vm.pickAnswer(dummyQuiz.questions[1], Dummy.country.copy(name = "EA"))
        vm.onNext()
        advanceUntilIdle()

        assertEquals(
            QuizUiState.ResultsPhase(QuizResults(1, 1, .5)),
            state
        )
    }


    private val dummyQuiz = Quiz(
        questions = listOf(
            QuizQuestion(
                QuestionDifficulty.TRIVIAL, Dummy.country.copy(name = "TA"), listOf(
                    Dummy.country.copy(name = "TA"),
                    Dummy.country.copy(name = "TB"),
                )
            ),
            QuizQuestion(
                QuestionDifficulty.EASY, Dummy.country.copy(name = "EA"), listOf(
                    Dummy.country.copy(name = "EA"),
                    Dummy.country.copy(name = "EB"),
                )
            )
        )
    )

}