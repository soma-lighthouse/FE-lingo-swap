package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.request.UploadFilterDTO
import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.model.response.FilterDTO
import com.lighthouse.android.data.model.response.UserProfileDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApiService {
    @GET("api/v1/user/{userId}/matches")
    suspend fun getMatchedUser(
        @Path("userId") userId: String,
        @Query("next") next: Int?,
        @Query("pageSize") pageSize: Int?,
    ): Response<BaseResponse<UserProfileDTO>>

    @GET("api/v1/user/{userId}/preference")
    suspend fun getFilterSetting(
        @Path("userId") userId: String,
    ): Response<BaseResponse<FilterDTO>>

    @PATCH("api/v1/user/{userId}/preference")
    suspend fun uploadFilterSetting(
        @Path("userId") userId: String,
        @Body filter: UploadFilterDTO,
    ): Response<BaseResponse<Void>>
}