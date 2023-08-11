package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.model.response.UserProfileDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApiService {
    @GET("api/v1/user/{userId}/matches")
    suspend fun getMatchedUser(
        @Path("userId") userId: Int,
        @Query("next") next: Int?,
        @Query("pageSize") pageSize: Int?,
    ): Response<BaseResponse<UserProfileDTO>>
}