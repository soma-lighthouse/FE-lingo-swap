package com.lighthouse.lingo_swap.di

import com.lighthouse.android.data.api.HomeApiService
import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import com.lighthouse.android.data.repository.datasourceimpl.HomeRemoteDataSourceImpl
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
    fun provideHomeRemoteDataSource(homeApiService: HomeApiService): HomeRemoteDataSource {
        return HomeRemoteDataSourceImpl(homeApiService)
    }

//    @Provides
//    @Singleton
//    fun provideDrivenDataSource(drivenApiService: DrivenApiService): DrivenRemoteDataSource {
//        return DrivenRemoteDataSourceImpl(drivenApiService)
//    }
}