package com.lighthouse.domain.repository

import com.lighthouse.domain.entity.response.vo.ChannelVO
import com.lighthouse.domain.entity.response.vo.ChatQuestionsVO
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun createChannel(opUserId: String): Flow<ChannelVO>
    fun leaveChannel(): Flow<Boolean>
    fun getRecommendedQuestions(categoryId: Int, nextId: Int?): Flow<ChatQuestionsVO>
}