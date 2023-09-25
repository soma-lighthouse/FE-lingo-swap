package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.request.CreateChannelDTO
import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.model.response.ChannelDTO
import com.lighthouse.android.data.model.response.ChatQuestionsDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatApiService {
    @POST("api/v1/chat")
    suspend fun createChannel(
        @Body userIds: CreateChannelDTO
    ): Response<BaseResponse<ChannelDTO>>

    @POST("api/v1/chat/leave")
    suspend fun leaveChannel(
        @Body body: Map<String, String>
    ): Response<BaseResponse<Void>>

    @GET("api/v1/question/recommendation")
    suspend fun getRecommendedQuestions(
        @Query("categoryId") categoryId: Int,
        @Query("next") nextId: Int?
    ): Response<BaseResponse<ChatQuestionsDTO>>
}
