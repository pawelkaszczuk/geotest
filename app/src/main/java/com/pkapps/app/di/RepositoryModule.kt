package com.pkapps.app.di

import com.pkapps.data.repository.CountryRepositoryImpl
import com.pkapps.ui.domain.repository.CountryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bind(impl: CountryRepositoryImpl): CountryRepository
}