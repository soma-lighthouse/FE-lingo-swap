package com.lighthouse.lingo_swap.di

import com.lighthouse.android.data.repository.PostRepositoryImpl
import com.lighthouse.android.data.repository.datasource.PostRemoteDataSource
import com.lighthouse.domain.repository.PostRepository
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
        postRemoteDataSource: PostRemoteDataSource,
    ): PostRepository {
        return PostRepositoryImpl(postRemoteDataSource)
    }
}