package com.pkapps.app.screen.welcome

import StandardTestDispatcherExtension
import com.pkapps.ui.domain.exception.NetworkException
import com.pkapps.ui.domain.model.Country
import com.pkapps.ui.domain.usecase.GetUserCountryUseCase
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, StandardTestDispatcherExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
internal class WelcomeViewModelTest {

    @MockK
    lateinit var getUserCountryUseCase: GetUserCountryUseCase

    private val vm by lazy { WelcomeViewModel(getUserCountryUseCase) }
    private val state: WelcomeUiState
        get() = vm.uiState.value

    @Test
    fun `WHEN vm is initialized THEN show the loading and load the data`() = runTest {
        coEvery { getUserCountryUseCase() } returns dummyCountry.copy(name = "simpleTest")
        assertEquals(WelcomeUiState.Loading, state)
        advanceUntilIdle()
        assertEquals(WelcomeUiState.Loaded(dummyCountry.copy(name = "simpleTest")), state)
    }

    @Test
    fun `WHEN there's a network error THAN show an error and allow to reload`() = runTest {
        coEvery { getUserCountryUseCase() } throws NetworkException()
        assertEquals(WelcomeUiState.Loading, state)
        advanceUntilIdle()
        assertEquals(WelcomeUiState.NetworkError, state)

        coEvery { getUserCountryUseCase() } coAnswers {
            delay(1_000)
            dummyCountry.copy(name = "retry")
        }
        vm.load()
        runCurrent()
        assertEquals(WelcomeUiState.Loading, state)
        advanceUntilIdle()
        assertEquals(WelcomeUiState.Loaded(dummyCountry.copy(name = "retry")), state)
    }



    private val dummyCountry = Country("", "", "", "", "", emptyList(), "", "")
}