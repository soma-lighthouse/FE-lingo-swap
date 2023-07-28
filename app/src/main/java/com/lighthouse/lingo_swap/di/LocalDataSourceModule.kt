package com.lighthouse.lingo_swap.di

import android.content.SharedPreferences
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.local.LocalPreferenceDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalDataSourceModule {
    @Provides
    @Singleton
    fun provideLocalDataSource(sharedPreferences: SharedPreferences): LocalPreferenceDataSource {
        return LocalPreferenceDataSourceImpl(sharedPreferences)
    }
}