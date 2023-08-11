package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.request.UpdateLikeDTO
import com.lighthouse.android.data.model.request.UploadQuestionDTO
import com.lighthouse.android.data.model.response.BoardDTO
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow

interface BoardRemoteDataSource {
    fun getBoardQuestions(category: Int, order: String?, page: Int): Flow<Resource<BoardDTO>>
    fun uploadQuestion(info: UploadQuestionDTO): Flow<Resource<String>>
    fun updateLike(questionId: Int, memberId: UpdateLikeDTO): Flow<Resource<String>>
}