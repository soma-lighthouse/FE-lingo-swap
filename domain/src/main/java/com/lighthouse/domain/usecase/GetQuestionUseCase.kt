package com.lighthouse.domain.usecase

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.UploadQuestionVO
import com.lighthouse.domain.repository.BoardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuestionUseCase @Inject constructor(
    private val repository: BoardRepository,
) {
    fun invoke(category: Int, order: String?, next: Int?, pageSize: Int?) =
        repository.getBoardQuestions(category, order, next, pageSize)


    fun uploadQuestion(info: UploadQuestionVO): Flow<Resource<String>> =
        repository.uploadQuestion(info)

    fun updateLike(questionId: Int, userId: String) {
        repository.updateLike(questionId, userId)
    }

    fun cancelLike(questionId: Int, userId: String) {
        repository.cancelLike(questionId, userId)
    }
}