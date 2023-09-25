package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.request.UploadQuestionDTO
import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.model.response.BoardDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BoardApiService {
    @GET("api/v1/question/category/{category}")
    suspend fun getQuestion(
        @Path("category") category: Int,
        @Query("order") order: String?,
        @Query("next") page: Int?,
        @Query("pageSize") pageSize: Int?,
    ): Response<BaseResponse<BoardDTO>>

    @POST("api/v1/question")
    suspend fun uploadQuestion(
        @Body info: UploadQuestionDTO,
    ): Response<BaseResponse<Void>>

    @POST("api/v1/question/{questionId}/like")
    suspend fun updateLike(
        @Path("questionId") questionId: Int,
    ): Response<Void>


    @DELETE("api/v1/question/{questionId}/like")
    suspend fun cancelLike(
        @Path("questionId") questionId: Int,
    ): Response<Void>
}