package com.pkapps.data.repository

import android.content.Context
import android.telephony.TelephonyManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pkapps.data.api.RestcountriesService
import com.pkapps.data.mapper.CountryMapper
import com.pkapps.ui.domain.model.Country
import com.pkapps.ui.domain.repository.CountryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val restcountriesService: RestcountriesService,
    private val countryMapper: CountryMapper,
    private val datastore: DataStore<Preferences>,
) : CountryRepository {

    override suspend fun getAll(): List<Country> {
        val countries = restcountriesService.getCountries()
        return countryMapper.mapCollection(countries)
    }

    override suspend fun getByCode(code: String): Country {
        val country = restcountriesService.getCountriesByCode(code).first()
        return countryMapper.map(country)
    }

    override suspend fun getHomeCountryCode(): String? = datastore.data.first()[MY_COUNTRY_KEY]

    override suspend fun saveHomeCountryCode(code: String) {
        datastore.edit { preferences ->
            preferences[MY_COUNTRY_KEY] = code
        }
    }

    // https://stackoverflow.com/a/19415296
    override suspend fun getUserCountryCodeFromSim(): String? {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simCountry = tm.simCountryIso

            if (simCountry != null && simCountry.length == 2) { // SIM country code is available
                return simCountry.lowercase()
            } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                val networkCountry = tm.networkCountryIso
                if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                    return networkCountry.lowercase()
                }
            }
        } catch (_: Exception) {
        }

        return null
    }

    companion object {
        private val MY_COUNTRY_KEY = stringPreferencesKey("my_country")
    }
}