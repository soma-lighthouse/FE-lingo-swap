package com.lighthouse.lingo_swap.di

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.lingo_swap.HeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class InterceptorModule {
    @Provides
    @Singleton
    fun provideHeaderInterceptor(localPreferenceDataSource: LocalPreferenceDataSource): HeaderInterceptor {
        return HeaderInterceptor(localPreferenceDataSource)
    }
}