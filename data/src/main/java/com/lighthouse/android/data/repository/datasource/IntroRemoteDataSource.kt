package com.lighthouse.android.data.repository.datasource

fun interface IntroRemoteDataSource {
    suspend fun getIntro(): String
}