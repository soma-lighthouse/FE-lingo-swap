package com.lighthouse.lingo_talk.di

import com.lighthouse.android.data.api.AuthApiService
import com.lighthouse.android.data.api.BoardApiService
import com.lighthouse.android.data.api.ChatApiService
import com.lighthouse.android.data.api.DrivenApiService
import com.lighthouse.android.data.api.HomeApiService
import com.lighthouse.android.data.api.ProfileApiService
import com.lighthouse.android.data.repository.datasource.AuthRemoteDataSource
import com.lighthouse.android.data.repository.datasource.BoardRemoteDataSource
import com.lighthouse.android.data.repository.datasource.ChatRemoteDataSource
import com.lighthouse.android.data.repository.datasource.DrivenRemoteDataSource
import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import com.lighthouse.android.data.repository.datasource.ProfileRemoteDataSource
import com.lighthouse.android.data.repository.datasourceimpl.AuthRemoteDataSourceImpl
import com.lighthouse.android.data.repository.datasourceimpl.BoardRemoteDataSourceImpl
import com.lighthouse.android.data.repository.datasourceimpl.ChatRemoteDataSourceImpl
import com.lighthouse.android.data.repository.datasourceimpl.DrivenRemoteDataSourceImpl
import com.lighthouse.android.data.repository.datasourceimpl.HomeRemoteDataSourceImpl
import com.lighthouse.android.data.repository.datasourceimpl.ProfileRemoteDataSourceImpl
import com.lighthouse.lingo_talk.di.annotation.Main
import com.lighthouse.lingo_talk.di.annotation.Test
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

    @Provides
    @Singleton
    fun provideProfileDataSource(@Main profileApiService: ProfileApiService): ProfileRemoteDataSource {
        return ProfileRemoteDataSourceImpl(profileApiService)
    }

    @Provides
    @Singleton
    fun provideAuthDataSource(@Main authApiService: AuthApiService): AuthRemoteDataSource {
        return AuthRemoteDataSourceImpl(authApiService)
    }

    @Provides
    @Singleton
    fun provideChatDataSource(@Main chatApiService: ChatApiService): ChatRemoteDataSource {
        return ChatRemoteDataSourceImpl(chatApiService)
    }
}