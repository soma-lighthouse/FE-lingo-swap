package com.lighthouse.lingo_swap.di

import com.google.gson.GsonBuilder
import com.lighthouse.android.data.api.DrivenApiService
import com.lighthouse.android.data.api.IntroAPIService
import com.lighthouse.android.data.api.interceptor.ContentInterceptor
import com.lighthouse.domain.response.ViewTypeVO
import com.lighthouse.lingo_swap.ViewTypeDeserializer
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
class NetModule {
//    @Provides
//    @Singleton
//    fun provideIntroRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl()
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .build()
//    }

    @Provides
    @Singleton
    fun provideIntroAPIService(retrofit: Retrofit): IntroAPIService {
        return retrofit.create(IntroAPIService::class.java)
    }

    @Provides
    @Singleton
    fun provideDrivenHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(ContentInterceptor)
            .addInterceptor(HttpLoggingInterceptor())
            .build()

    @Provides
    @Singleton
    fun provideDrivenRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("")
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(ViewTypeVO::class.java, ViewTypeDeserializer())
                        .create()
                )
            )

            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideDrivenAPIService(retrofit: Retrofit): DrivenApiService =
        retrofit.create(DrivenApiService::class.java)
}
