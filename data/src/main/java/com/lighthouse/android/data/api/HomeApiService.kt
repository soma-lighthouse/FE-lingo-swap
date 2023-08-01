package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.BaseResponse
import com.lighthouse.android.data.model.UserProfileDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApiService {
    @GET("api/v1/user")
    suspend fun getMatchedUser(
        @Query("page") page: Int,
    ): Response<BaseResponse<UserProfileDTO>>
}