package com.pkapps.data.mapper

import com.pkapps.data.model.CountryModel
import com.pkapps.ui.domain.model.Country
import javax.inject.Inject

class CountryMapper @Inject constructor() {

    fun mapCollection(data: List<CountryModel>) = data.sortedBy { it.name.common }.map(::map)

    fun map(data: CountryModel) = Country(name = data.name.common,
        cca2 = data.cca2,
        cca3 = data.cca3,
        flag = data.flags.png,
        flagDescription = data.flags.alt,
        neighborsCca3 = data.borders,
        region = data.region,
        subregion = data.subregion
    )

}