package com.pkapps.ui.domain.usecase

import com.pkapps.ui.domain.repository.CountryRepository
import javax.inject.Inject


class GetFilteredCountriesUseCase @Inject constructor(
    private val countryRepository: CountryRepository
) {
    suspend operator fun invoke(query: String = "") = countryRepository.getAll()
        .filter { it.name.startsWith(query, ignoreCase = true) }
}