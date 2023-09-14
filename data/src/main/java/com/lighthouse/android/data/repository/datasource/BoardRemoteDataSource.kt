package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.request.UploadQuestionDTO
import com.lighthouse.android.data.model.response.BoardDTO
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow

interface BoardRemoteDataSource {
    fun getBoardQuestions(
        category: Int,
        order: String?,
        page: Int?,
        pageSize: Int?,
    ): Flow<Resource<BoardDTO>>

    fun uploadQuestion(info: UploadQuestionDTO): Flow<Resource<String>>
    fun updateLike(questionId: Int, memberId: Map<String, String>): Flow<Resource<Boolean>>
    fun cancelLike(questionId: Int): Flow<Resource<Boolean>>
}