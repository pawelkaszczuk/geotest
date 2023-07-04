package com.pkapps.app.screen.countrypicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pkapps.app.common.updateAs
import com.pkapps.ui.domain.exception.NetworkException
import com.pkapps.ui.domain.model.Country
import com.pkapps.ui.domain.usecase.GetFilteredCountriesUseCase
import com.pkapps.ui.domain.usecase.SetHomeCountryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryPickerViewModel @Inject constructor(
    private val getFilteredCountries: GetFilteredCountriesUseCase,
    private val setHomeCountry: SetHomeCountryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CountryPickerUiState>(CountryPickerUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            if (uiState.value is CountryPickerUiState.NetworkError) {
                _uiState.update { CountryPickerUiState.Loading }
            }

            _uiState.update {
                try {
                    CountryPickerUiState.Loaded(
                        countries = getFilteredCountries(),
                        selected = null
                    )
                } catch (_: NetworkException) {
                    CountryPickerUiState.NetworkError
                }
            }
        }
    }

    fun select(country: Country) {
        _uiState.updateAs { state: CountryPickerUiState.Loaded ->
            state.copy(selected = country)
        }
    }

    fun confirmChoice() {
        viewModelScope.launch {
            _uiState.updateAs { state: CountryPickerUiState.Loaded ->
                try {
                    val selected = state.selected ?: return@launch
                    setHomeCountry(selected)
                    CountryPickerUiState.ConfirmedChoice
                } catch (_: NetworkException) {
                    CountryPickerUiState.NetworkError
                }
            }
        }
    }

    fun toggleSearch() {
        viewModelScope.launch {
            _uiState.updateAs { state: CountryPickerUiState.Loaded ->
                if (state.showSearch) {
                    state.copy(
                        showSearch = false,
                        countries = getFilteredCountries(),
                        searchQuery = ""
                    )
                } else {
                    state.copy(
                        showSearch = true
                    )
                }
            }
        }
    }

    fun clearSearch() {
        updateSearchQuery("")
    }

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            _uiState.updateAs { state: CountryPickerUiState.Loaded ->
                state.copy(
                    searchQuery = query,
                    countries = getFilteredCountries(query)
                )
            }
        }
    }
}