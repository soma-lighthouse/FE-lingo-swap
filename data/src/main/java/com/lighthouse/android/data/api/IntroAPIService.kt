package com.lighthouse.android.data.api

import retrofit2.http.GET

fun interface IntroAPIService {
    @GET("/home")
    suspend fun getIntro(): String
}