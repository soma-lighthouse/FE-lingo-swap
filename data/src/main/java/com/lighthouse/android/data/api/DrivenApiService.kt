package com.lighthouse.android.data.api

import com.lighthouse.domain.response.server_driven.ViewTypeVO
import retrofit2.Response
import retrofit2.http.GET

fun interface DrivenApiService {
    @GET("/home")
    suspend fun getDriven(): Response<List<ViewTypeVO>>
}