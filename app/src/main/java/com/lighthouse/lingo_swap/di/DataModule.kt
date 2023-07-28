package com.lighthouse.lingo_swap.di

import com.lighthouse.android.data.repository.HomeRepositoryImpl
import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import com.lighthouse.domain.repository.HomeRepository
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
    fun provideHomeRepository(
        homeRemoteDataSource: HomeRemoteDataSource,
    ): HomeRepository {
        return HomeRepositoryImpl(homeRemoteDataSource)
    }

//    @Provides
//    @Singleton
//    fun provideDrivenRepository(
//        drivenRemoteDataSource: DrivenRemoteDataSource
//    ): DrivenRepository {
//        return DrivenRepositoryImpl(drivenRemoteDataSource)
//    }
}