package com.lighthouse.lingo_swap.di

import com.lighthouse.domain.repository.IntroRepository
import com.lighthouse.domain.usecase.GetIntroUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    @Provides
    @Singleton
    fun provideGetPostUseCase(postRepository: IntroRepository): GetIntroUseCase {
        return GetIntroUseCase(postRepository)
    }
}