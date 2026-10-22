package com.example.ch10.di

import com.example.ch10.data.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

// Saat uji instrumented, repository sungguhan diganti fake — tanpa internet
// dan tanpa API key. NewsAppHiltTest tidak perlu tahu pergantian ini.
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
abstract class FakeRepositoryModule {

    @Binds
    abstract fun bindNewsRepository(fake: FakeNewsRepository): NewsRepository
}
