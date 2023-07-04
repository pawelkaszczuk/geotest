package com.pkapps.ui.domain.usecase

import com.pkapps.ui.domain.model.Country
import com.pkapps.ui.domain.repository.CountryRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class GetUserCountryUseCase @Inject constructor(
    private val countryRepository: CountryRepository
) {
    suspend operator fun invoke(): Country {
        val code = flow {
            emit(countryRepository.getHomeCountryCode())
            emit(countryRepository.getUserCountryCodeFromSim())
            emit(DEFAULT_COUNTRY)
        }
            .filterNotNull()
            .first()

        return countryRepository.getByCode(code)
    }

    companion object {
        const val DEFAULT_COUNTRY = "US"
    }
}