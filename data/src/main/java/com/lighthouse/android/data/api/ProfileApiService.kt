package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.response.ProfileDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApiService {
    @GET("api/v1/user/{userId}/profile")
    suspend fun getProfileDetail(
        @Path("userId") userId: String,
    ): Response<ProfileDTO>
}