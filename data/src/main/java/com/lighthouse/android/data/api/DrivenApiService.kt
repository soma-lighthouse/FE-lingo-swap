package com.lighthouse.android.data.api

import com.lighthouse.domain.response.ViewTypeVO
import retrofit2.Response
import retrofit2.http.GET

fun interface DrivenApiService {
    @GET("/test")
    suspend fun getDriven(): Response<List<ViewTypeVO>>
}