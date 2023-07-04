package com.pkapps.app.screen.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pkapps.ui.domain.exception.NetworkException
import com.pkapps.ui.domain.usecase.GetUserCountryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val getUserCountry: GetUserCountryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WelcomeUiState>(WelcomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            if (uiState.value is WelcomeUiState.NetworkError) {
                _uiState.update { WelcomeUiState.Loading }
            }

            _uiState.update {
                try {
                    WelcomeUiState.Loaded(country = getUserCountry())
                } catch (_: NetworkException) {
                    WelcomeUiState.NetworkError
                }
            }
        }
    }
}