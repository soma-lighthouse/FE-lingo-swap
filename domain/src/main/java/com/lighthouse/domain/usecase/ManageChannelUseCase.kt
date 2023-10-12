package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.ChatRepository
import javax.inject.Inject

class ManageChannelUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    fun createChannel(opUserId: String) = chatRepository.createChannel(opUserId)
    fun leaveChannel() = chatRepository.leaveChannel()

    fun getRecommendedQuestions(categoryId: Int, nextId: Int?) =
        chatRepository.getRecommendedQuestions(categoryId, nextId)
}