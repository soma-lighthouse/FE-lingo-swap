package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.request.UploadQuestionDTO
import com.lighthouse.android.data.model.response.BoardDTO
import kotlinx.coroutines.flow.Flow

interface BoardRemoteDataSource {
    fun getBoardQuestions(
        category: Int,
        order: String?,
        page: Int?,
        pageSize: Int?,
    ): Flow<BoardDTO>

    fun uploadQuestion(info: UploadQuestionDTO): Flow<Boolean>
    fun updateLike(questionId: Int): Flow<Boolean>
    fun cancelLike(questionId: Int): Flow<Boolean>
}