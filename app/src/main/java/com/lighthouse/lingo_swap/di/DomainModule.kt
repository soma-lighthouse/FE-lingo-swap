package com.lighthouse.lingo_swap.di

import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.BoardRepository
import com.lighthouse.domain.repository.DrivenRepository
import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.domain.repository.ProfileRepository
import com.lighthouse.domain.usecase.GetAuthUseCase
import com.lighthouse.domain.usecase.GetMatchedUserUseCase
import com.lighthouse.domain.usecase.GetProfileUseCase
import com.lighthouse.domain.usecase.GetQuestionUseCase
import com.lighthouse.domain.usecase.TestUseCase
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

    @Provides
    @Singleton
    fun provideProfileUseCase(profileRepository: ProfileRepository): GetProfileUseCase {
        return GetProfileUseCase(profileRepository)
    }

    @Provides
    @Singleton
    fun provideAuthUseCase(authRepository: AuthRepository): GetAuthUseCase {
        return GetAuthUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideTestUseCase(drivenRepository: DrivenRepository): TestUseCase {
        return TestUseCase(drivenRepository)
    }
}