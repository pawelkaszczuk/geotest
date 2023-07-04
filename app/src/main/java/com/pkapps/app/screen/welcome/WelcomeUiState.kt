package com.pkapps.app.screen.welcome

import com.pkapps.ui.domain.model.Country

sealed class WelcomeUiState {
    object Loading : WelcomeUiState()
    object NetworkError : WelcomeUiState()
    data class Loaded(
        val country: Country
    ) : WelcomeUiState()
}