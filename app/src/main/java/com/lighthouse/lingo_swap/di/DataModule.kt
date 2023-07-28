package com.lighthouse.lingo_swap.di

import com.lighthouse.android.data.api.HomeApiService
import com.lighthouse.android.data.repository.DrivenRepositoryImpl
import com.lighthouse.android.data.repository.HomeRepositoryImpl
import com.lighthouse.android.data.repository.datasource.DrivenRemoteDataSource
import com.lighthouse.domain.repository.DrivenRepository
import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.lingo_swap.di.Annotation.Main
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Singleton
    fun provideDrivenRepository(
        drivenRemoteDataSource: DrivenRemoteDataSource,
    ): DrivenRepository {
        return DrivenRepositoryImpl(drivenRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(
        @Main api: HomeApiService,
    ): HomeRepository {
        return HomeRepositoryImpl(api)
    }
}