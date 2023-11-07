package com.lighthouse.domain.repository

import com.lighthouse.domain.entity.request.UploadQuestionVO
import com.lighthouse.domain.entity.response.vo.BoardVO
import kotlinx.coroutines.flow.Flow

interface BoardRepository {
    fun getBoardQuestions(
        category: Int,
        order: String?,
        page: Int?,
        pageSize: Int?,
    ): Flow<BoardVO>

    fun uploadQuestion(info: UploadQuestionVO): Flow<Boolean>
    fun updateLike(questionId: Int)

    fun cancelLike(questionId: Int)
}