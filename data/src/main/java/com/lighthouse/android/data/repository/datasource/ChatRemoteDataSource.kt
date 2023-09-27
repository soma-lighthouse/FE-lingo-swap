package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.response.ChannelDTO
import com.lighthouse.android.data.model.response.ChatQuestionsDTO
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow

interface ChatRemoteDataSource {
    fun createChannel(opUserId: String, myUserId: String): Flow<Resource<ChannelDTO>>
    fun leaveChannel(myUserId: String): Flow<Resource<Boolean>>

    fun getRecommendedQuestions(categoryId: Int, nextId: Int?): Flow<Resource<ChatQuestionsDTO>>
}