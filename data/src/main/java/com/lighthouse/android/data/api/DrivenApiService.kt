package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.model.response.ProfileDTO
import retrofit2.Response
import retrofit2.http.GET

fun interface DrivenApiService {
    @GET("api/v1/user/1/match")
    suspend fun getDriven(): Response<BaseResponse<ProfileDTO>>
}