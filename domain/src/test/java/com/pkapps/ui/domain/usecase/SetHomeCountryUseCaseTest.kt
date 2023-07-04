package com.pkapps.ui.domain.usecase

import com.pkapps.ui.domain.repository.CountryRepository
import domain.Dummy
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class SetHomeCountryUseCaseTest {

    @MockK
    lateinit var countryRepository: CountryRepository

    val useCase by lazy {
        SetHomeCountryUseCase(countryRepository)
    }

    @Test
    fun `WHEN use case is called THEN save the country`() = runTest {
        coJustRun { countryRepository.saveHomeCountryCode("pl") }

        useCase(Dummy.country.copy(cca3 = "pl"))
        coVerify(exactly = 1) { countryRepository.saveHomeCountryCode("pl") }
    }
}