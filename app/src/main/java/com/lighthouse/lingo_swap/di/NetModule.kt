package com.lighthouse.lingo_swap.di

import com.lighthouse.android.data.api.AuthApiService
import com.lighthouse.android.data.api.BoardApiService
import com.lighthouse.android.data.api.ChatApiService
import com.lighthouse.android.data.api.DrivenApiService
import com.lighthouse.android.data.api.HomeApiService
import com.lighthouse.android.data.api.ProfileApiService
import com.lighthouse.android.data.api.interceptor.AuthInterceptor
import com.lighthouse.lingo_swap.BuildConfig
import com.lighthouse.lingo_swap.HeaderInterceptor
import com.lighthouse.lingo_swap.NullOrEmptyConverter
import com.lighthouse.lingo_swap.di.annotation.Main
import com.lighthouse.lingo_swap.di.annotation.Test
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetModule {
    @Provides
    @Singleton
    @Main
    fun provideLightHouseHttpClient(
        headerInterceptor: HeaderInterceptor,
        authInterceptor: AuthInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(headerInterceptor)
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    @Main
    fun provideLightHouseRetrofit(@Main okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.LIGHTHOUSE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(NullOrEmptyConverter())
            .build()
    }


    @Provides
    @Singleton
    @Test
    fun provideDrivenHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
//            .addInterceptor(ContentInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    @Test
    fun provideDrivenRetrofit(@Test okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.TEST_BASE_URL)
            .client(okHttpClient)
//            .addConverterFactory(
//                GsonConverterFactory.create(
//                    GsonBuilder()
//                        .registerTypeAdapter(ViewTypeVO::class.java, ViewTypeDeserializer())
//                        .create()
//                )
//            )

            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Test
    fun provideDrivenAPIService(@Test retrofit: Retrofit): DrivenApiService =
        retrofit.create(DrivenApiService::class.java)

    @Provides
    @Singleton
    @Main
    fun provideHomeService(@Main retrofit: Retrofit): HomeApiService {
        return retrofit.create(HomeApiService::class.java)
    }

    @Provides
    @Singleton
    @Main
    fun provideBoardService(@Main retrofit: Retrofit): BoardApiService {
        return retrofit.create(BoardApiService::class.java)
    }

    @Provides
    @Singleton
    @Main
    fun provideProfileService(@Main retrofit: Retrofit): ProfileApiService {
        return retrofit.create(ProfileApiService::class.java)
    }

    @Provides
    @Singleton
    @Main
    fun provideAuthService(@Main retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    @Main
    fun provideChatService(@Main retrofit: Retrofit): ChatApiService {
        return retrofit.create(ChatApiService::class.java)
    }
}
