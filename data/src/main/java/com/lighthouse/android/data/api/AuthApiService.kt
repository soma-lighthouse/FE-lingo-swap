package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.request.RegisterInfoDTO
import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.model.response.CountryForm
import com.lighthouse.android.data.model.response.InterestForm
import com.lighthouse.android.data.model.response.LanguageForm
import com.lighthouse.android.data.model.response.PreSignedUrlDTO
import com.lighthouse.android.data.model.response.UserTokenDTO
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface AuthApiService {
    @GET("api/v1/form/interests")
    suspend fun getInterestList(
        @Query("id_token") idToken: String,
    ): Response<BaseResponse<InterestForm>>

    @GET("api/v1/form/country")
    suspend fun getCountryList(
        @Query("id_token") idToken: String,
    ): Response<BaseResponse<CountryForm>>

    @GET("api/v1/form/language")
    suspend fun getLanguageList(
        @Query("id_token") idToken: String,
    ): Response<BaseResponse<LanguageForm>>

    @POST("api/v1/user/upload/profile")
    suspend fun getPreSignedURL(
        @Body imageName: Map<String, String>,
    ): Response<BaseResponse<PreSignedUrlDTO>>

    @POST("api/v1/auth/signup")
    suspend fun registerUser(
        @Query("id_token") idToken: String,
        @Body info: RegisterInfoDTO,
    ): Response<BaseResponse<UserTokenDTO>>

    @PUT
    suspend fun uploadImg(
        @Url url: String,
        @Body profilePath: RequestBody,
    ): Response<Void>

    @POST("api/v1/auth/login")
    suspend fun postGoogleLogin(
        @Query("id_token") idToken: String
    ): Response<BaseResponse<UserTokenDTO>>

    @PATCH("api/v1/user/{uuid}/profile/image")
    suspend fun updateImgInfo(
        @Path("uuid") uuid: String,
        @Body imageUrl: Map<String, String>,
    ): Response<Void>
}