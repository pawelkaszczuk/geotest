package com.pkapps.ui.domain.usecase

import com.pkapps.ui.domain.dict.QuestionDifficulty
import com.pkapps.ui.domain.dict.QuestionDifficulty.Companion.next
import com.pkapps.ui.domain.model.Country
import com.pkapps.ui.domain.model.Quiz
import com.pkapps.ui.domain.model.QuizQuestion
import com.pkapps.ui.domain.repository.CountryRepository
import java.util.Random
import javax.inject.Inject

class GenerateQuizUseCase @Inject constructor(
    private val countryRepository: CountryRepository,
    private val getCountriesByDifficulty: GetCountriesByDifficultyUseCase
) {

    private fun Map<QuestionDifficulty, List<Country>>.getQuestionWithMinimumDifficulty(
        minimumDifficulty: QuestionDifficulty,
        notFrom: List<Country>
    ): CountryWithDifficulty? {
        return generateSequence(minimumDifficulty) { it.next() }
            .firstNotNullOfOrNull { difficulty ->
                this[difficulty]
                    ?.firstOrNull { country -> !notFrom.contains(country) }
                    ?.let { CountryWithDifficulty(it, difficulty) }
            }
    }

    suspend operator fun invoke(
        structure: List<QuestionDifficulty> = buildList {
            repeat(times = 1) { add(QuestionDifficulty.TRIVIAL) }
            repeat(times = 2) { add(QuestionDifficulty.EASY) }
            repeat(times = 2) { add(QuestionDifficulty.MEDIUM) }
            repeat(times = 4) { add(QuestionDifficulty.HARD) }
        },
        incorrectAnswersPerQuestion: Int = 3,
        random: Random = Random()
    ): Quiz {
        val countries = countryRepository.getAll()
        val countriesByDifficulty = getCountriesByDifficulty().mapValues { it.value.shuffled(random) }
        val questions = mutableListOf<QuizQuestion>()

        for (difficulty in structure) {
            val correctAnswer =
                countriesByDifficulty.getQuestionWithMinimumDifficulty(difficulty, questions.map { it.correctAnswer }) ?: continue

            val incorrectAnswers = countries.shuffled(random)
                .filter { it != correctAnswer.country }
                .take(incorrectAnswersPerQuestion)

            questions.add(
                QuizQuestion(
                    difficulty = correctAnswer.difficulty,
                    correctAnswer = correctAnswer.country,
                    options = buildList {
                        add(correctAnswer.country);
                        addAll(incorrectAnswers)
                    }.shuffled(random)
                )
            )
        }

        return Quiz(
            questions = questions
        )
    }

    private data class CountryWithDifficulty(val country: Country, val difficulty: QuestionDifficulty)
}