package com.lighthouse.lingo_swap.di

import com.lighthouse.android.data.repository.DrivenRepositoryImpl
import com.lighthouse.android.data.repository.IntroRepositoryImpl
import com.lighthouse.android.data.repository.datasource.DrivenRemoteDataSource
import com.lighthouse.android.data.repository.datasource.IntroRemoteDataSource
import com.lighthouse.domain.repository.DrivenRepository
import com.lighthouse.domain.repository.IntroRepository
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
    fun providePostRepository(
        introRemoteDataSource: IntroRemoteDataSource,
    ): IntroRepository {
        return IntroRepositoryImpl(introRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideDrivenRepository(
        drivenRemoteDataSource: DrivenRemoteDataSource
    ): DrivenRepository {
        return DrivenRepositoryImpl(drivenRemoteDataSource)
    }
}