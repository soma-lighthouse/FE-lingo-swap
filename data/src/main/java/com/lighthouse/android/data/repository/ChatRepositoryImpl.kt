package com.lighthouse.android.data.repository

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.repository.datasource.ChatRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.ChatQuestionsVO
import com.lighthouse.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDataSource: ChatRemoteDataSource,
    private val localDataSource: LocalPreferenceDataSource,
) : ChatRepository {
    override fun createChannel(opUserId: String): Flow<Resource<Boolean>> =
        chatDataSource.createChannel(opUserId, localDataSource.getUUID().toString())
            .map {
                when (it) {
                    is Resource.Success -> {
                        Resource.Success(true)
                    }

                    is Resource.Error -> Resource.Error(it.message!!)
                }
            }

    override fun leaveChannel(): Flow<Resource<Boolean>> =
        chatDataSource.leaveChannel(localDataSource.getUUID().toString())
            .map {
                when (it) {
                    is Resource.Success -> {
                        Resource.Success(true)
                    }

                    is Resource.Error -> Resource.Error(it.message!!)
                }
            }

    override fun getRecommendedQuestions(
        categoryId: Int,
        nextId: Int?
    ): Flow<Resource<ChatQuestionsVO>> =
        chatDataSource.getRecommendedQuestions(categoryId, nextId)
            .map {
                when (it) {
                    is Resource.Success -> {
                        Resource.Success(it.data!!.toVO())
                    }

                    is Resource.Error -> Resource.Error(it.message!!)
                }
            }
}