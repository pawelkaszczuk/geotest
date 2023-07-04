package com.pkapps.app.screen.countrypicker

import StandardTestDispatcherExtension
import com.pkapps.ui.domain.exception.NetworkException
import com.pkapps.ui.domain.usecase.GetFilteredCountriesUseCase
import com.pkapps.ui.domain.usecase.SetHomeCountryUseCase
import domain.Dummy
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, StandardTestDispatcherExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
internal class CountryPickerViewModelTest {
    @MockK
    private lateinit var getFilteredCountriesUseCase: GetFilteredCountriesUseCase

    @MockK
    private lateinit var setHomeCountryUseCase: SetHomeCountryUseCase

    private lateinit var vm : CountryPickerViewModel
    private val state: CountryPickerUiState
        get() = vm.uiState.value


    @BeforeEach
    fun before() {
        coEvery { getFilteredCountriesUseCase() } returns listOf(
            Dummy.country.copy(name = "A"),
            Dummy.country.copy(name = "B"),
            Dummy.country.copy(name = "C")
        )

        vm = CountryPickerViewModel(getFilteredCountriesUseCase, setHomeCountryUseCase)
    }

    @Test
    fun `WHEN vm is initialized THEN show the loading and load the data`() = runTest {
        vm = CountryPickerViewModel(getFilteredCountriesUseCase, setHomeCountryUseCase)
        Assertions.assertEquals(CountryPickerUiState.Loading, state)
        advanceUntilIdle()
        Assertions.assertEquals(
            CountryPickerUiState.Loaded(
                showSearch = false,
                searchQuery = "",
                countries = listOf(
                    Dummy.country.copy(name = "A"),
                    Dummy.country.copy(name = "B"),
                    Dummy.country.copy(name = "C")
                ),
                selected = null
            ),
            state
        )
    }


    @Test
    fun `WHEN there's a network error THEN show an error and allow to reload`() = runTest {
        coEvery { getFilteredCountriesUseCase() } throws NetworkException()
        vm = CountryPickerViewModel(getFilteredCountriesUseCase, setHomeCountryUseCase)
        Assertions.assertEquals(CountryPickerUiState.Loading, state)
        advanceUntilIdle()
        Assertions.assertEquals(CountryPickerUiState.NetworkError, state)

        coEvery { getFilteredCountriesUseCase() } coAnswers {
            delay(1_000)
            listOf(
                Dummy.country.copy(name = "A"),
                Dummy.country.copy(name = "B"),
                Dummy.country.copy(name = "C")
            )
        }
        vm.load()
        runCurrent()
        Assertions.assertEquals(CountryPickerUiState.Loading, state)
        advanceUntilIdle()
        Assertions.assertEquals(
            CountryPickerUiState.Loaded(
                showSearch = false,
                searchQuery = "",
                countries = listOf(
                    Dummy.country.copy(name = "A"),
                    Dummy.country.copy(name = "B"),
                    Dummy.country.copy(name = "C")
                ),
                selected = null
            ), state
        )
    }

    @Test
    fun `WHEN user picks a country THEN mark selected country`() = runTest {
        vm.select(Dummy.country.copy(name = "C"))
        advanceUntilIdle()
        Assertions.assertEquals(
            CountryPickerUiState.Loaded(
                showSearch = false,
                searchQuery = "",
                countries = listOf(
                    Dummy.country.copy(name = "A"),
                    Dummy.country.copy(name = "B"),
                    Dummy.country.copy(name = "C")
                ),
                selected = Dummy.country.copy(name = "C")
            ),
            state
        )
    }

    @Test
    fun `WHEN user confirms THEN save the choose`() = runTest {
        coJustRun { setHomeCountryUseCase(Dummy.country.copy(name = "C")) }

        vm.select(Dummy.country.copy(name = "C"))
        advanceUntilIdle()

        vm.confirmChoice()
        advanceUntilIdle()

        Assertions.assertEquals(CountryPickerUiState.ConfirmedChoice, state)
        coVerify(exactly = 1) { setHomeCountryUseCase(Dummy.country.copy(name = "C")) }
    }

    @Test
    fun `WHEN user toggles search for the first time THEN show the search`() = runTest {
        vm.toggleSearch()
        advanceUntilIdle()

        Assertions.assertEquals(true, (state as? CountryPickerUiState.Loaded)?.showSearch)
    }

    @Test
    fun `WHEN user toggles search for the second time THEN hide the search`() = runTest {
        vm.toggleSearch()
        advanceUntilIdle()
        vm.toggleSearch()
        advanceUntilIdle()

        Assertions.assertEquals(false, (state as? CountryPickerUiState.Loaded)?.showSearch)
    }

    @Test
    fun `WHEN user updates search query THEN return filtered list`() = runTest {
        coEvery { getFilteredCountriesUseCase("A") } returns listOf(
            Dummy.country.copy(name = "A")
        )
        coEvery { getFilteredCountriesUseCase("B") } returns listOf(
            Dummy.country.copy(name = "B")
        )


        vm.toggleSearch()
        advanceUntilIdle()

        vm.updateSearchQuery("A")
        advanceUntilIdle()
        Assertions.assertEquals(
            listOf(Dummy.country.copy(name = "A")),
            (state as? CountryPickerUiState.Loaded)?.countries
        )

        vm.updateSearchQuery("B")
        advanceUntilIdle()
        Assertions.assertEquals(
            listOf(Dummy.country.copy(name = "B")),
            (state as? CountryPickerUiState.Loaded)?.countries
        )

        vm.clearSearch()
        advanceUntilIdle()
        Assertions.assertEquals(
            listOf(
                Dummy.country.copy(name = "A"),
                Dummy.country.copy(name = "B"),
                Dummy.country.copy(name = "C"),
            ), (state as? CountryPickerUiState.Loaded)?.countries
        )
    }
}