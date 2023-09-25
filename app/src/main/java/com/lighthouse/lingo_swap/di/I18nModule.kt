package com.lighthouse.lingo_swap.di

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.lighthousei18n.I18nManager
import com.lighthouse.lingo_swap.i18n.I18nManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object I18nModule {
    @Provides
    @Singleton
    fun provideI18nManager(localPreferenceDataSource: LocalPreferenceDataSource): I18nManager {
        return I18nManagerImpl(localPreferenceDataSource)
    }
}