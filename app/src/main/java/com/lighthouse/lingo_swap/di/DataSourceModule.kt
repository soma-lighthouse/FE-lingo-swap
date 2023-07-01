package com.lighthouse.lingo_swap.di

import com.lighthouse.android.data.repository.datasourceimpl.PostRemoteDataSourceImpl
import com.lighthouse.android.data.api.PostAPIService
import com.lighthouse.android.data.repository.datasource.PostRemoteDataSource
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
    fun provideRemoteDataSource(postAPIService: PostAPIService): PostRemoteDataSource {
        return PostRemoteDataSourceImpl(postAPIService)
    }
}