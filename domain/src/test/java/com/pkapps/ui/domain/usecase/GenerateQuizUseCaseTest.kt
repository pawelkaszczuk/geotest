package com.pkapps.ui.domain.usecase

import com.pkapps.ui.domain.dict.QuestionDifficulty
import com.pkapps.ui.domain.repository.CountryRepository
import domain.Dummy
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Random

@ExtendWith(MockKExtension::class)
internal class GenerateQuizUseCaseTest {

    @MockK
    lateinit var countryRepository: CountryRepository

    @MockK
    lateinit var getCountriesByDifficulty: GetCountriesByDifficultyUseCase

    val useCase by lazy { GenerateQuizUseCase(countryRepository, getCountriesByDifficulty) }

    @Test
    fun `WHEN we have enough countries THEN return counties by the structure parameter`() = runTest {
        coEvery { countryRepository.getAll() } returns emptyList()
        coEvery { getCountriesByDifficulty() } returns mapOf(
            QuestionDifficulty.EASY to listOf(Dummy.country.copy(name = "easy")),
            QuestionDifficulty.MEDIUM to listOf(Dummy.country.copy(name = "medium")),
            QuestionDifficulty.HARD to listOf(Dummy.country.copy(name = "hard"))
        )

        val results = useCase.invoke(listOf(QuestionDifficulty.MEDIUM, QuestionDifficulty.HARD, QuestionDifficulty.EASY))
        assertEquals("medium", results.questions[0].correctAnswer.name)
        assertEquals("hard", results.questions[1].correctAnswer.name)
        assertEquals("easy", results.questions[2].correctAnswer.name)
    }

    @Test
    fun `WHEN there's not enough easy countries THEN pick harder instead`() = runTest {
        coEvery { countryRepository.getAll() } returns emptyList()
        coEvery { getCountriesByDifficulty() } returns mapOf(
            QuestionDifficulty.MEDIUM to listOf(Dummy.country.copy(name = "medium")),
            QuestionDifficulty.HARD to listOf(Dummy.country.copy(name = "hard"))
        )

        val results = useCase.invoke(listOf(QuestionDifficulty.EASY, QuestionDifficulty.MEDIUM))
        assertEquals("medium", results.questions[0].correctAnswer.name)
        assertEquals("hard", results.questions[1].correctAnswer.name)
    }

    @Test
    fun `WHEN useCase is called with a different incorrectAnswers param THEN return correct amount of incorrect answers`() = runTest {
        coEvery { countryRepository.getAll() } returns listOf(
            Dummy.country.copy(name = "incorrectA"),
            Dummy.country.copy(name = "incorrectB"),
            Dummy.country.copy(name = "incorrectC"),
            Dummy.country.copy(name = "incorrectD"),
        )
        coEvery { getCountriesByDifficulty() } returns mapOf(
            QuestionDifficulty.TRIVIAL to listOf(
                Dummy.country.copy(name = "correct"),
            )
        )

        val results = useCase.invoke(listOf(QuestionDifficulty.TRIVIAL), incorrectAnswersPerQuestion = 2)
        assertEquals(3, results.questions[0].options.size)
        assertEquals(1, results.questions[0].options.count { it.name == "correct" })
        assertEquals(2, results.questions[0].options.count { it.name.startsWith("incorrect") })
    }

    @Test
    fun `WHEN incorrectAnswers param is too big THEN return maximum amount of incorrect answers`() = runTest {
        coEvery { countryRepository.getAll() } returns listOf(
            Dummy.country.copy(name = "incorrect"),
        )
        coEvery { getCountriesByDifficulty() } returns mapOf(
            QuestionDifficulty.TRIVIAL to listOf(
                Dummy.country.copy(name = "correct"),
            )
        )

        val results = useCase.invoke(listOf(QuestionDifficulty.TRIVIAL), incorrectAnswersPerQuestion = 99)
        assertEquals(2, results.questions[0].options.size)
        assertEquals(1, results.questions[0].options.count { it.name == "correct" })
        assertEquals(1, results.questions[0].options.count { it.name == "incorrect" })
    }

    @Test
    fun `WHEN correct answer is returned twice by repository THEN show it only once`() = runTest {
        coEvery { countryRepository.getAll() } returns listOf(
            Dummy.country.copy(name = "incorrect"),
            Dummy.country.copy(name = "correct"),
        )
        coEvery { getCountriesByDifficulty() } returns mapOf(
            QuestionDifficulty.TRIVIAL to listOf(
                Dummy.country.copy(name = "correct"),
            )
        )

        val results = useCase.invoke(listOf(QuestionDifficulty.TRIVIAL), incorrectAnswersPerQuestion = 3)
        assertEquals(2, results.questions[0].options.size)
        assertEquals(1, results.questions[0].options.count { it.name == "correct" })
        assertEquals(1, results.questions[0].options.count { it.name == "incorrect" })
    }

    @Test
    fun `WHEN the use case is called THEN the data should be randomized`() = runTest {
        val wrongOptions = listOf(
            Dummy.country.copy(name = "incorrectA"),
            Dummy.country.copy(name = "incorrectB"),
            Dummy.country.copy(name = "incorrectC"),
            Dummy.country.copy(name = "incorrectD"),
            Dummy.country.copy(name = "incorrectE"),
            Dummy.country.copy(name = "incorrectF"),
        )

        val questions = mapOf(
            QuestionDifficulty.TRIVIAL to listOf(
                Dummy.country.copy(name = "trivial1"),
                Dummy.country.copy(name = "trivial2"),
                Dummy.country.copy(name = "trivial3"),
                Dummy.country.copy(name = "trivial4"),
            ),
            QuestionDifficulty.MEDIUM to listOf(
                Dummy.country.copy(name = "medium1"),
                Dummy.country.copy(name = "medium2"),
                Dummy.country.copy(name = "medium3"),
                Dummy.country.copy(name = "medium4"),
            )
        )

        coEvery { getCountriesByDifficulty() } returns questions
        coEvery { countryRepository.getAll() } returns wrongOptions

        val random = Random(1337)
        val results = useCase(
            structure = listOf(QuestionDifficulty.TRIVIAL, QuestionDifficulty.MEDIUM, QuestionDifficulty.TRIVIAL, QuestionDifficulty.MEDIUM),
            incorrectAnswersPerQuestion = 2,
            random = Random(1337)
        )

        val randomisedQuestions = questions.mapValues { it.value.shuffled(random) }
        
        assertEquals(randomisedQuestions.getValue(QuestionDifficulty.TRIVIAL)[0], results.questions[0].correctAnswer)
        assertEquals((listOf(results.questions[0].correctAnswer) + wrongOptions.shuffled(random).take(2)).shuffled(random), results.questions[0].options)
        assertEquals(randomisedQuestions.getValue(QuestionDifficulty.MEDIUM)[0], results.questions[1].correctAnswer)
        assertEquals((listOf(results.questions[1].correctAnswer) + wrongOptions.shuffled(random).take(2)).shuffled(random), results.questions[1].options)
        assertEquals(randomisedQuestions.getValue(QuestionDifficulty.TRIVIAL)[1], results.questions[2].correctAnswer)
        assertEquals((listOf(results.questions[2].correctAnswer) + wrongOptions.shuffled(random).take(2)).shuffled(random), results.questions[2].options)
        assertEquals(randomisedQuestions.getValue(QuestionDifficulty.MEDIUM)[1], results.questions[3].correctAnswer)
        assertEquals((listOf(results.questions[3].correctAnswer) + wrongOptions.shuffled(random).take(2)).shuffled(random), results.questions[3].options)
    }
}