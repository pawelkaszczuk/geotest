package com.pkapps.ui.domain.usecase

import com.pkapps.ui.domain.model.Country
import com.pkapps.ui.domain.repository.CountryRepository
import javax.inject.Inject


class SetHomeCountryUseCase @Inject constructor(
    private val countryRepository: CountryRepository
) {
    suspend operator fun invoke(country: Country) {
        countryRepository.saveHomeCountryCode(country.cca3)
    }
}