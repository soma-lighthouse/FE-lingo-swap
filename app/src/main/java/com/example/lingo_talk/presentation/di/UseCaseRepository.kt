package com.example.lingo_talk.presentation.di

import com.example.lingo_talk.domain.repository.PostRepository
import com.example.lingo_talk.domain.usecase.GetPostUseCase
import com.example.lingo_talk.domain.usecase.SavePostUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseRepository {
    @Provides
    @Singleton
    fun provideGetPostUseCase(postRepository: PostRepository): GetPostUseCase {
        return GetPostUseCase(postRepository)
    }

    @Provides
    @Singleton
    fun provideSavePostUseCase(postRepository: PostRepository): SavePostUseCase {
        return SavePostUseCase(postRepository)
    }
}