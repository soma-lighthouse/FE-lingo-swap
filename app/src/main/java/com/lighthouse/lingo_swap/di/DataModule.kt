package com.lighthouse.lingo_swap.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.lighthouse.android.data.api.HomeApiService
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.local.LocalPreferenceDataSourceImpl
import com.lighthouse.android.data.repository.DrivenRepositoryImpl
import com.lighthouse.android.data.repository.HomeRepositoryImpl
import com.lighthouse.android.data.repository.datasource.DrivenRemoteDataSource
import com.lighthouse.domain.repository.DrivenRepository
import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.lingo_swap.HeaderInterceptor
import com.lighthouse.lingo_swap.di.Annotation.Main
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
        @Main api: HomeApiService,
    ): HomeRepository {
        return HomeRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideHeaderInterceptor(localPreferenceDataSource: LocalPreferenceDataSource): HeaderInterceptor {
        return HeaderInterceptor(localPreferenceDataSource)
    }

    @Provides
    @Singleton
    fun provideShared(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

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