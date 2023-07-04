package com.pkapps.ui.domain.usecase

import com.pkapps.ui.domain.repository.CountryRepository
import domain.Dummy
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class GetFilteredCountriesUseCaseTest {

    @MockK
    lateinit var countryRepository: CountryRepository

    val useCase by lazy {
        GetFilteredCountriesUseCase(countryRepository)
    }

    @Test
    fun `WHEN use case is called THEN return filtered countries by name`() = runTest {
        coEvery { countryRepository.getAll() } returns listOf(
            Dummy.country.copy(name = "AA"),
            Dummy.country.copy(name = "AB"),
            Dummy.country.copy(name = "CA"),
        )

        assertEquals(listOf(
            Dummy.country.copy(name = "AA"),
            Dummy.country.copy(name = "AB"),
        ), useCase("a"))
    }
}