package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.BaseResponse
import com.lighthouse.android.data.model.BoardDTO
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BoardApiService {
    @GET("api/board/v1/question/{category}")
    suspend fun getQuestion(
        @Path("category") category: Int,
        @Query("order") order: String,
        @Query("page") page: Int,
    ): Response<BaseResponse<BoardDTO>>

    @POST("api/board/v1/question")
    suspend fun uploadQuestion(
        @Field("memberId") userId: Int,
        @Field("categoryId") categoryId: Int,
        @Field("content") content: String,
    ): Response<BaseResponse<String>>

    @POST("api/board/v1/question/{questionId}/addLike")
    suspend fun updateLike(
        @Path("questionId") questionId: Int,
        @Field("memberId") memberId: Int,
    ): Response<BaseResponse<String>>

}