package com.lighthouse.android.data.repository

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.repository.datasource.ChatRemoteDataSource
import com.lighthouse.android.data.util.LocalKey
import com.lighthouse.domain.entity.response.vo.ChannelVO
import com.lighthouse.domain.entity.response.vo.ChatQuestionsVO
import com.lighthouse.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDataSource: ChatRemoteDataSource,
    private val localDataSource: LocalPreferenceDataSource,
) : ChatRepository {
    override fun createChannel(opUserId: String): Flow<ChannelVO> =
        chatDataSource.createChannel(
            opUserId,
            localDataSource.getString(LocalKey.USER_ID)
        ).map {
            it.toVO()
        }

    override fun leaveChannel(): Flow<Boolean> =
        chatDataSource.leaveChannel(localDataSource.getString(LocalKey.USER_ID))


    override fun getRecommendedQuestions(
        categoryId: Int,
        nextId: Int?
    ): Flow<ChatQuestionsVO> =
        chatDataSource.getRecommendedQuestions(categoryId, nextId)
            .map {
                it.toVO()
            }
}
