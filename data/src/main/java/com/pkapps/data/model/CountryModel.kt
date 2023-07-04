package com.pkapps.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CountryModel(
    @SerialName("name") val name: Name,
    @SerialName("cca2") val cca2: String,
    @SerialName("cca3") val cca3: String,
    @SerialName("independent") val independent: Boolean? = null,
    @SerialName("unMember") val unMember: Boolean,
    @SerialName("currencies") val currencies: Map<String, Currency> = emptyMap(),
    @SerialName("capital") val capital: List<String> = emptyList(),
    @SerialName("region") val region: String,
    @SerialName("subregion") val subregion: String? = null,
    @SerialName("borders") val borders: List<String> = emptyList(),
    @SerialName("population") val population: Int,
    @SerialName("continents") val continents: List<String>,
    @SerialName("flags") val flags: Flags,
    @SerialName("coatOfArms") val coatOfArms: CoatOfArms
) {

    @Serializable
    data class Name(
        @SerialName("common") val common: String,
        @SerialName("official") val official: String
    )

    @Serializable
    data class Currency(
        @SerialName("name") val name: String? = null,
        @SerialName("symbol") val symbol: String? = null
    )

    @Serializable
    data class Flags(
        @SerialName("png") val png: String,
        @SerialName("svg") val svg: String,
        @SerialName("alt") val alt: String
    )

    @Serializable
    data class CoatOfArms(
        @SerialName("png") val png: String?,
        @SerialName("svg") val svg: String?
    )
}



