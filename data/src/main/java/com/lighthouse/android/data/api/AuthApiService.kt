package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.model.response.InterestDTO
import retrofit2.Response
import retrofit2.http.GET

interface AuthApiService {
    @GET("api/v1/auth")
    suspend fun getInterestList(): Response<BaseResponse<List<InterestDTO>>>
}