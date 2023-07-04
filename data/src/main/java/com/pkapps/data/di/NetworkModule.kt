package com.pkapps.data.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.pkapps.ui.domain.exception.NetworkException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext appContext: Context) =
        PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile("settings") }
        )

    @Provides
    @Singleton
    fun provideHttpClient() =
        HttpClient(OkHttp) {
            expectSuccess = true
            engine {
                //if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.HEADERS
                    }
                    addInterceptor(loggingInterceptor)
                //}
            }
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "restcountries.com"
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    // we want to be lenient because it's an external api
                    ignoreUnknownKeys = true
                    @OptIn(ExperimentalSerializationApi::class)
                    explicitNulls = false
                    isLenient = true
                })
            }
            install(HttpCache)
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
            HttpResponseValidator {
                handleResponseExceptionWithRequest { exception, _ ->
                    throw NetworkException(exception)
                }
            }
        }
}