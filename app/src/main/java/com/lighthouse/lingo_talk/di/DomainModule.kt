package com.lighthouse.lingo_talk.di

import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.DrivenRepository
import com.lighthouse.domain.repository.ProfileRepository
import com.lighthouse.domain.usecase.CheckLoginStatusUseCase
import com.lighthouse.domain.usecase.TestUseCase
import com.lighthouse.domain.usecase.UpdateUserProfileUseCase
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
    fun provideUpdateUserProfileUseCase(
        profileRepository: ProfileRepository,
        authRepository: AuthRepository
    ): UpdateUserProfileUseCase {
        return UpdateUserProfileUseCase(profileRepository, authRepository)
    }

    @Provides
    @Singleton
    fun provideTestUseCase(drivenRepository: DrivenRepository): TestUseCase {
        return TestUseCase(drivenRepository)
    }

    @Provides
    @Singleton
    fun provideCheckLoginStatusUseCase(authRepository: AuthRepository): CheckLoginStatusUseCase {
        return CheckLoginStatusUseCase(authRepository)
    }
}