package com.lighthouse.lingo_swap.di

import com.lighthouse.domain.repository.BoardRepository
import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.domain.usecase.GetMatchedUserUseCase
import com.lighthouse.domain.usecase.GetQuestionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    @Singleton
    fun provideHomeUseCase(homeRepository: HomeRepository): GetMatchedUserUseCase {
        return GetMatchedUserUseCase(homeRepository)
    }

    @Provides
    @Singleton
    fun provideBoardUseCase(boardRepository: BoardRepository): GetQuestionUseCase {
        return GetQuestionUseCase(boardRepository)
    }
}