package com.lighthouse.lingo_swap.di

import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.domain.usecase.GetMatchedUserUseCase
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
    fun provideHomeUseCase(homeRepository: HomeRepository): GetMatchedUserUseCase {
        return GetMatchedUserUseCase(homeRepository)
    }
}