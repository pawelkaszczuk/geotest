package com.pkapps.ui.domain.usecase

import com.pkapps.ui.domain.dict.QuestionDifficulty
import com.pkapps.ui.domain.model.Country
import com.pkapps.ui.domain.repository.CountryRepository
import javax.inject.Inject

class GetCountriesByDifficultyUseCase @Inject constructor(
    private val countyRepository: CountryRepository,
    private val getUserCountryUseCase: GetUserCountryUseCase
) {

    suspend operator fun invoke(): Map<QuestionDifficulty, List<Country>> {
        val userCountry = getUserCountryUseCase()
        return countyRepository.getAll()
            .filter { it != userCountry }
            .groupBy { country ->
                when {
                    userCountry.neighborsCca3.contains(country.cca3) -> QuestionDifficulty.TRIVIAL
                    userCountry.subregion == country.subregion -> QuestionDifficulty.EASY
                    userCountry.region == country.region -> QuestionDifficulty.MEDIUM
                    else -> QuestionDifficulty.HARD
                }
            }
    }
}