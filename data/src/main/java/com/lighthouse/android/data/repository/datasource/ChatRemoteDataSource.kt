package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.response.ChannelDTO
import com.lighthouse.android.data.model.response.ChatQuestionsDTO
import kotlinx.coroutines.flow.Flow

interface ChatRemoteDataSource {
    fun createChannel(opUserId: String, myUserId: String): Flow<ChannelDTO>
    fun leaveChannel(myUserId: String): Flow<Boolean>

    fun getRecommendedQuestions(categoryId: Int, nextId: Int?): Flow<ChatQuestionsDTO>
}