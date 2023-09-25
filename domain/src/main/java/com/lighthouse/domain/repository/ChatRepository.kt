package com.lighthouse.domain.repository

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.ChatQuestionsVO
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun createChannel(opUserId: String): Flow<Resource<Boolean>>
    fun leaveChannel(): Flow<Resource<Boolean>>
    fun getRecommendedQuestions(categoryId: Int, nextId: Int?): Flow<Resource<ChatQuestionsVO>>
}