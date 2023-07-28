package com.lighthouse.lingo_swap.di

import com.lighthouse.android.data.api.DrivenApiService
import com.lighthouse.android.data.repository.datasource.DrivenRemoteDataSource
import com.lighthouse.android.data.repository.datasourceimpl.DrivenRemoteDataSourceImpl
import com.lighthouse.lingo_swap.di.Annotation.Test
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Provides
    @Singleton
    fun provideDrivenDataSource(@Test drivenApiService: DrivenApiService): DrivenRemoteDataSource {
        return DrivenRemoteDataSourceImpl(drivenApiService)
    }
}