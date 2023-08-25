package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.request.RegisterInfoDTO
import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.model.response.CountryForm
import com.lighthouse.android.data.model.response.InterestForm
import com.lighthouse.android.data.model.response.LanguageForm
import com.lighthouse.android.data.model.response.PreSignedURL
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface AuthApiService {
    @GET("api/v1/user/form/interests")
    suspend fun getInterestList(): Response<BaseResponse<InterestForm>>

    @GET("api/v1/user/form/country")
    suspend fun getCountryList(): Response<BaseResponse<CountryForm>>

    @GET("api/v1/user/form/language")
    suspend fun getLanguageList(): Response<BaseResponse<LanguageForm>>

    @POST("api/v1/user/upload/profile")
    suspend fun getPreSignedURL(
        @Body imageName: Map<String, String>,
    ): Response<BaseResponse<PreSignedURL>>

    @POST("api/v1/user")
    suspend fun registerUser(
        @Body info: RegisterInfoDTO,
    ): Response<BaseResponse<String?>>

    @PUT
    suspend fun uploadImg(
        @Url url: String,
        @Body profilePath: RequestBody,
    ): Response<Void>
}