package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.BaseResponse
import com.lighthouse.android.data.model.UserProfileDTO
import retrofit2.Response
import retrofit2.http.GET

interface HomeApiService {
    @GET("user")
    suspend fun getMatchedUser(): Response<BaseResponse<UserProfileDTO>>
}