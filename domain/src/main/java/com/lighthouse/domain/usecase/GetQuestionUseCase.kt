package com.lighthouse.domain.usecase

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.repository.BoardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuestionUseCase @Inject constructor(
    private val repository: BoardRepository,
) {
    fun invoke(category: Int, order: String, page: Int) =
        repository.getBoardQuestions(category, order, page)


    fun uploadQuestion(userId: Int, categoryId: Int, content: String): Flow<Resource<String>> =
        repository.uploadQuestion(userId, categoryId, content)

    fun updateLike(questionId: Int, memberId: Int): Flow<Resource<String>> =
        repository.updateLike(questionId, memberId)

}