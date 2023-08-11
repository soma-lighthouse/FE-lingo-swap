package com.lighthouse.domain.repository

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.request.UpdateLikeVO
import com.lighthouse.domain.request.UploadQuestionVO
import com.lighthouse.domain.response.vo.BoardVO
import kotlinx.coroutines.flow.Flow

interface BoardRepository {
    fun getBoardQuestions(category: Int, order: String?, page: Int): Flow<Resource<BoardVO>>
    fun uploadQuestion(info: UploadQuestionVO): Flow<Resource<String>>
    fun updateLike(questionId: Int, memberId: UpdateLikeVO): Flow<Resource<String>>
}