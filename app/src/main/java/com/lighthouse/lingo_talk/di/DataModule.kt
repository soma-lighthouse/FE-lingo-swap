package com.lighthouse.lingo_talk.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.lighthouse.android.data.api.interceptor.AuthInterceptor
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.local.LocalPreferenceDataSourceImpl
import com.lighthouse.android.data.repository.AuthRepositoryImpl
import com.lighthouse.android.data.repository.BoardRepositoryImpl
import com.lighthouse.android.data.repository.ChatRepositoryImpl
import com.lighthouse.android.data.repository.DrivenRepositoryImpl
import com.lighthouse.android.data.repository.HomeRepositoryImpl
import com.lighthouse.android.data.repository.ProfileRepositoryImpl
import com.lighthouse.android.data.repository.datasource.AuthRemoteDataSource
import com.lighthouse.android.data.repository.datasource.BoardRemoteDataSource
import com.lighthouse.android.data.repository.datasource.ChatRemoteDataSource
import com.lighthouse.android.data.repository.datasource.DrivenRemoteDataSource
import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import com.lighthouse.android.data.repository.datasource.ProfileRemoteDataSource
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.BoardRepository
import com.lighthouse.domain.repository.ChatRepository
import com.lighthouse.domain.repository.DrivenRepository
import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.domain.repository.ProfileRepository
import com.lighthouse.lighthousei18n.I18nManager
import com.lighthouse.lingo_talk.HeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDrivenRepository(
        drivenRemoteDataSource: DrivenRemoteDataSource,
    ): DrivenRepository {
        return DrivenRepositoryImpl(drivenRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(
        homeRemoteDataSource: HomeRemoteDataSource,
        local: LocalPreferenceDataSource,
    ): HomeRepository {
        return HomeRepositoryImpl(homeRemoteDataSource, local)
    }

    @Provides
    @Singleton
    fun provideBoardRepository(
        boardRemoteDataSource: BoardRemoteDataSource,
        localPreferenceDataSource: LocalPreferenceDataSource
    ): BoardRepository {
        return BoardRepositoryImpl(boardRemoteDataSource, localPreferenceDataSource)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileRemoteDataSource: ProfileRemoteDataSource,
        localPreferenceDataSource: LocalPreferenceDataSource,
    ): ProfileRepository {
        return ProfileRepositoryImpl(profileRemoteDataSource, localPreferenceDataSource)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        localPreferenceDataSource: LocalPreferenceDataSource,
        authRemoteDataSource: AuthRemoteDataSource,
    ): AuthRepository {
        return AuthRepositoryImpl(localPreferenceDataSource, authRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        chatRemoteDataSource: ChatRemoteDataSource,
        localPreferenceDataSource: LocalPreferenceDataSource,
    ): ChatRepository {
        return ChatRepositoryImpl(chatRemoteDataSource, localPreferenceDataSource)
    }

    @Provides
    @Singleton
    fun provideHeaderInterceptor(
        localPreferenceDataSource: LocalPreferenceDataSource,
        i18nManager: I18nManager,
    ): HeaderInterceptor {
        return HeaderInterceptor(localPreferenceDataSource, i18nManager)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(localPreferenceDataSource: LocalPreferenceDataSource): AuthInterceptor {
        return AuthInterceptor(localPreferenceDataSource)
    }

    @Provides
    @Singleton
    fun provideShared(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        return EncryptedSharedPreferences.create(
            context,
            "encrypted-setting",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(sharedPreferences: SharedPreferences): LocalPreferenceDataSource {
        return LocalPreferenceDataSourceImpl(sharedPreferences)
    }
}