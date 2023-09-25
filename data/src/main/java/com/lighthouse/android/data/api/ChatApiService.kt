package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.model.response.ChatQuestionsDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {
    @POST("api/v1/chat")
    suspend fun createChannel(
        @Body userIds: Map<String, List<String>>
    ): Response<BaseResponse<Void>>

    @POST("api/v1/chat/leave")
    suspend fun leaveChannel(
        @Body body: Map<String, String>
    ): Response<BaseResponse<Void>>

    @GET("api/v1/question/recommendation/{categoryId}")
    suspend fun getRecommendedQuestions(
        @Path("categoryId") categoryId: Int,
        @Query("next") nextId: Int?
    ): Response<BaseResponse<ChatQuestionsDTO>>
}