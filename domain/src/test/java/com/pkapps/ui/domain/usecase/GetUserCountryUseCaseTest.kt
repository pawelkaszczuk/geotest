package com.pkapps.ui.domain.usecase

import com.pkapps.ui.domain.repository.CountryRepository
import domain.Dummy
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class GetUserCountryUseCaseTest {

    @MockK
    lateinit var countryRepository: CountryRepository

    val useCase by lazy { GetUserCountryUseCase(countryRepository) }

    @Test
    fun `WHEN user configured the home country THEN use the saved value`() = runTest {
        coEvery { countryRepository.getHomeCountryCode() } returns "saved"
        coEvery { countryRepository.getByCode(eq("saved")) } returns Dummy.country.copy(name = "MY")

        assertEquals(Dummy.country.copy(name = "MY"), useCase())

        coVerify(exactly = 0) { countryRepository.getUserCountryCodeFromSim() }
    }

    @Test
    fun `WHEN the home country is unknown THEN try to guess the country from the sim`() = runTest {
        coEvery { countryRepository.getHomeCountryCode() } returns null
        coEvery { countryRepository.getUserCountryCodeFromSim() } returns "a"
        coEvery { countryRepository.getByCode(eq("a")) } returns Dummy.country.copy(name = "A")

        assertEquals(Dummy.country.copy(name = "A"), useCase())
    }

    @Test
    fun `WHEN we have no idea about the user's country THEN just use the USA`() = runTest {
        coEvery { countryRepository.getHomeCountryCode() } returns null
        coEvery { countryRepository.getUserCountryCodeFromSim() } returns null
        coEvery { countryRepository.getByCode(eq("US")) } returns Dummy.country.copy(name = "US")

        assertEquals(Dummy.country.copy(name = "US"), useCase())
    }
}