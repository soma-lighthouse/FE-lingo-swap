package com.lighthouse.domain.usecase

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.repository.BoardRepository
import com.lighthouse.domain.entity.request.UpdateLikeVO
import com.lighthouse.domain.entity.request.UploadQuestionVO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuestionUseCase @Inject constructor(
    private val repository: BoardRepository,
) {
    fun invoke(category: Int, order: String?, page: Int) =
        repository.getBoardQuestions(category, order, page)


    fun uploadQuestion(info: UploadQuestionVO): Flow<Resource<String>> =
        repository.uploadQuestion(info)

    fun updateLike(questionId: Int, memberId: UpdateLikeVO): Flow<Resource<String>> =
        repository.updateLike(questionId, memberId)

}