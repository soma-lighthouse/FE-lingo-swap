package com.lighthouse.lingo_talk.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.lighthouse.android.data.api.interceptor.AuthInterceptor
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.local.LocalPreferenceDataSourceImpl
import com.lighthouse.android.data.remote.RemoteConfigDataSource
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
import com.lighthouse.lingo_talk.BuildConfig
import com.lighthouse.lingo_talk.R
import com.lighthouse.lingo_talk.interceptor.HeaderInterceptor
import com.lighthouse.lingo_talk.interceptor.RemoteConfigInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class DefaultPreference

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class EncryptedPreference

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
        remoteConfigDataSource: RemoteConfigDataSource
    ): HomeRepository {
        return HomeRepositoryImpl(homeRemoteDataSource, local, remoteConfigDataSource)
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
    fun provideAuthInterceptor(
        localPreferenceDataSource: LocalPreferenceDataSource, remoteConfig: FirebaseRemoteConfig
    ): AuthInterceptor {
        return AuthInterceptor(localPreferenceDataSource, remoteConfig)
    }

    @Provides
    @Singleton
    fun provideRemoteInterceptor(
        remoteConfigDataSource: RemoteConfigDataSource
    ): RemoteConfigInterceptor {
        return RemoteConfigInterceptor(remoteConfigDataSource)
    }

    @Provides
    @Singleton
    fun provideShared(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        return try {
            createSharedPreference(context, masterKey)
        } catch (gsException: GeneralSecurityException) {
            deleteSharedPreference(context)
            createSharedPreference(context, masterKey)
        }
    }

    private fun createSharedPreference(context: Context, masterKey: MasterKey): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            context.packageName + "_secured_preferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private fun deleteSharedPreference(context: Context) {
        try {
            val check =
                context.deleteSharedPreferences(context.packageName + "_secured_preferences")

            clearSharedPreference(context)

            if (check) {
                Log.d("EncrytedSharedPref", "sharedPref deleted")
            } else {
                Log.d("EncrytedSharedPref", "sharedPref not exists")
            }

            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            keyStore.deleteEntry(MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        } catch (e: Exception) {
            Log.d("EncrytedSharedPref", "Error occured while deleting sharedPref")
        }
    }

    private fun clearSharedPreference(context: Context) {
        context.getSharedPreferences(
            context.packageName + "_secured_preferences",
            Context.MODE_PRIVATE
        ).edit().clear().apply()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(sharedPreferences: SharedPreferences): LocalPreferenceDataSource {
        return LocalPreferenceDataSourceImpl(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = FirebaseRemoteConfig.getInstance().apply {
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 60 else 3600)
                .build()
            setDefaultsAsync(R.xml.remote_default_config)
            setConfigSettingsAsync(configSettings)
        }
        return remoteConfig
    }
}