package com.pkapps.data.api

import com.pkapps.data.model.CountryModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject


class RestcountriesService @Inject constructor(
    private val client: HttpClient
    ) {

    suspend fun getCountries() =
        client.get("https://restcountries.com/v3.1/independent?status=true").body<List<CountryModel>>()

    /**
     * @param code cca2, ccn3, cca3 or cioc country code
     */
    suspend fun getCountriesByCode(code: String) =
        client.get("https://restcountries.com/v3.1/alpha/$code").body<List<CountryModel>>()
}