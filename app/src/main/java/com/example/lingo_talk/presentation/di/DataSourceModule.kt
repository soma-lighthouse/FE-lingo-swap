package com.example.lingo_talk.presentation.di

import com.example.lingo_talk.data.api.PostAPIService
import com.example.lingo_talk.data.db.PostDAO
import com.example.lingo_talk.data.repository.datasource.PostCacheDataSource
import com.example.lingo_talk.data.repository.datasource.PostLocalDataSource
import com.example.lingo_talk.data.repository.datasource.PostRemoteDataSource
import com.example.lingo_talk.data.repository.datasourceImpl.PostCacheDataSourceImpl
import com.example.lingo_talk.data.repository.datasourceImpl.PostLocalDataSourceImpl
import com.example.lingo_talk.data.repository.datasourceImpl.PostRemoteDataSourceImpl
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
    fun provideCacheDataSource(): PostCacheDataSource {
        return PostCacheDataSourceImpl()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(postDAO: PostDAO): PostLocalDataSource {
        return PostLocalDataSourceImpl(postDAO)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(postAPIService: PostAPIService): PostRemoteDataSource {
        return PostRemoteDataSourceImpl(postAPIService)
    }
}