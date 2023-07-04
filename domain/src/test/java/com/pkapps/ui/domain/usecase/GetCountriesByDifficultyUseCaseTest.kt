package com.pkapps.ui.domain.usecase

import com.pkapps.ui.domain.dict.QuestionDifficulty
import com.pkapps.ui.domain.repository.CountryRepository
import domain.Dummy
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class GetCountriesByDifficultyUseCaseTest {

    @MockK
    lateinit var countryRepository: CountryRepository

    @MockK
    lateinit var getUserCountryUseCase: GetUserCountryUseCase

    val useCase by lazy {
        GetCountriesByDifficultyUseCase(countryRepository, getUserCountryUseCase)
    }

    @Test
    fun `WHEN generating questions for a quiz THEN return countries by difficulty`() = runTest {

        val userCountry = Dummy.country.copy(
            cca3 = "AE5", region = "A", subregion = "E", neighborsCca3 = listOf("AE6", "BA1")
        )
        coEvery { countryRepository.getAll() } returns listOf(
            userCountry,
            Dummy.country.copy(cca3 = "AE6", region = "A", subregion = "E"),
            Dummy.country.copy(cca3 = "BA1", region = "B", subregion = "A"),
            Dummy.country.copy(cca3 = "AE7", region = "A", subregion = "E"),
            Dummy.country.copy(cca3 = "AE8", region = "A", subregion = "E"),
            Dummy.country.copy(cca3 = "AF", region = "A", subregion = "F"),
            Dummy.country.copy(cca3 = "C", region = "C", subregion = "C")
        )

        coEvery { getUserCountryUseCase() } returns userCountry

        val results = useCase()


        Assertions.assertEquals("AE6", results.getValue(QuestionDifficulty.TRIVIAL)[0].cca3)
        Assertions.assertEquals("BA1", results.getValue(QuestionDifficulty.TRIVIAL)[1].cca3)
        Assertions.assertEquals("AE7", results.getValue(QuestionDifficulty.EASY)[0].cca3)
        Assertions.assertEquals("AE8", results.getValue(QuestionDifficulty.EASY)[1].cca3)
        Assertions.assertEquals("AF", results.getValue(QuestionDifficulty.MEDIUM)[0].cca3)
        Assertions.assertEquals("C", results.getValue(QuestionDifficulty.HARD)[0].cca3)
        Assertions.assertEquals(6, results.flatMap { it.value }.count())
    }

}