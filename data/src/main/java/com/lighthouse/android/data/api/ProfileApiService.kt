package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.request.UpdateProfileDTO
import com.lighthouse.android.data.model.request.UploadFilterDTO
import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.model.response.MyQuestionResponse
import com.lighthouse.android.data.model.response.ProfileDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ProfileApiService {
    @GET("api/v1/user/{userId}/profile")
    suspend fun getProfileDetail(
        @Path("userId") userId: String,
    ): Response<BaseResponse<ProfileDTO>>

    @GET("api/v1/user/{userId}/question")
    suspend fun getMyQuestions(
        @Path("userId") userId: String,
    ): Response<BaseResponse<MyQuestionResponse>>

    @PATCH("api/v1/user/{userId}/profile")
    suspend fun updateProfile(
        @Path("userId") userId: String,
        @Body newProfile: UpdateProfileDTO,
    ): Response<BaseResponse<Void>>

    @PATCH("api/v1/user/{userId}/preference")
    suspend fun updateFilter(
        @Path("userId") userId: String,
        @Body newFilter: UploadFilterDTO,
    ): Response<BaseResponse<Void>>
}