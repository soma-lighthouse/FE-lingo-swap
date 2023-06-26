package com.example.lingo_talk.presentation.di

import com.example.lingo_talk.data.repository.PostRepositoryImpl
import com.example.lingo_talk.data.repository.datasource.PostCacheDataSource
import com.example.lingo_talk.data.repository.datasource.PostLocalDataSource
import com.example.lingo_talk.data.repository.datasource.PostRemoteDataSource
import com.example.lingo_talk.domain.repository.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun providePostRepository(
        postRemoteDataSource: PostRemoteDataSource,
        postLocalDataSource: PostLocalDataSource,
        postCacheDataSource: PostCacheDataSource
    ): PostRepository {
        return PostRepositoryImpl(postRemoteDataSource, postLocalDataSource, postCacheDataSource)
    }
}