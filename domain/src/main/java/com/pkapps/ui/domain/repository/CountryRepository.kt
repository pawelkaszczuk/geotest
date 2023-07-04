package com.pkapps.ui.domain.repository

import com.pkapps.ui.domain.model.Country

interface CountryRepository {
    suspend fun getAll(): List<Country>
    suspend fun getByCode(code: String): Country
    suspend fun getHomeCountryCode(): String?
    suspend fun saveHomeCountryCode(code: String)
    suspend fun getUserCountryCodeFromSim(): String?
}