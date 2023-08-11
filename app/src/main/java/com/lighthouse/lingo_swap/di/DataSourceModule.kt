package com.lighthouse.lingo_swap.di

import com.lighthouse.android.data.api.BoardApiService
import com.lighthouse.android.data.api.DrivenApiService
import com.lighthouse.android.data.api.HomeApiService
import com.lighthouse.android.data.repository.datasource.BoardRemoteDataSource
import com.lighthouse.android.data.repository.datasource.DrivenRemoteDataSource
import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import com.lighthouse.android.data.repository.datasourceimpl.BoardRemoteDataSourceImpl
import com.lighthouse.android.data.repository.datasourceimpl.DrivenRemoteDataSourceImpl
import com.lighthouse.android.data.repository.datasourceimpl.HomeRemoteDataSourceImpl
import com.lighthouse.lingo_swap.di.Annotation.Main
import com.lighthouse.lingo_swap.di.Annotation.Test
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideDrivenDataSource(@Test drivenApiService: DrivenApiService): DrivenRemoteDataSource {
        return DrivenRemoteDataSourceImpl(drivenApiService)
    }

    @Provides
    @Singleton
    fun provideHomeDataSource(@Main homeApiService: HomeApiService): HomeRemoteDataSource {
        return HomeRemoteDataSourceImpl(homeApiService)
    }

    @Provides
    @Singleton
    fun provideBoardDataSource(@Main boardApiService: BoardApiService): BoardRemoteDataSource {
        return BoardRemoteDataSourceImpl(boardApiService)
    }
}