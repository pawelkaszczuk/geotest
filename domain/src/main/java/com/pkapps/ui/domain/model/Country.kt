package com.pkapps.ui.domain.model

data class Country(
    val name: String,
    val cca2: String,
    val cca3: String,
    val flag: String?,
    val flagDescription: String?,
    val neighborsCca3: List<String>,
    val region: String,
    val subregion: String?
)
