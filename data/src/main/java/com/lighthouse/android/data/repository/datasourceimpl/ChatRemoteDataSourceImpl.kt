package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.ChatApiService
import com.lighthouse.android.data.model.response.ChatQuestionsDTO
import com.lighthouse.android.data.repository.datasource.ChatRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChatRemoteDataSourceImpl @Inject constructor(
    private val chatApiService: ChatApiService,
) : ChatRemoteDataSource, NetworkResponse() {
    override fun createChannel(opUserId: String, myUserId: String): Flow<Resource<Boolean>> = flow {
        val response =
            chatApiService.createChannel(
                mapOf("userIds" to listOf(myUserId, opUserId))
            )

        if (response.isSuccessful) {
            emit(Resource.Success(true))
        } else {
            throw errorHandler(response)
        }
    }

    override fun leaveChannel(myUserId: String): Flow<Resource<Boolean>> = flow {
        val response = chatApiService.leaveChannel(mapOf("userId" to myUserId))
        if (response.isSuccessful) {
            emit(Resource.Success(true))
        } else {
            throw errorHandler(response)
        }
    }

    override fun getRecommendedQuestions(
        categoryId: Int,
        nextId: Int?
    ): Flow<Resource<ChatQuestionsDTO>> = flow {
        val response = chatApiService.getRecommendedQuestions(categoryId, nextId)
        if (response.isSuccessful) {
            emit(changeResult(response))
        } else {
            throw errorHandler(response)
        }
    }
}