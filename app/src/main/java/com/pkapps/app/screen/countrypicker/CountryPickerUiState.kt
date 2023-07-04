package com.pkapps.app.screen.countrypicker

import com.pkapps.ui.domain.model.Country

sealed class CountryPickerUiState {
    object Loading : CountryPickerUiState()
    object NetworkError : CountryPickerUiState()
    data class Loaded(
        val showSearch: Boolean = false,
        val searchQuery: String = "",
        val countries: List<Country>,
        val selected: Country?
    ) : CountryPickerUiState()
    object ConfirmedChoice : CountryPickerUiState()
}