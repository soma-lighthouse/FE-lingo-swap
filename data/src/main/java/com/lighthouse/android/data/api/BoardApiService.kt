package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.BaseResponse
import com.lighthouse.android.data.model.BoardDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BoardApiService {
    @GET("api/v1/question/[category]")
    suspend fun getQuestion(
        @Path("category") category: Int,
        order: String,
        @Query("page") page: Int,
    ): Response<BaseResponse<BoardDTO>>
}