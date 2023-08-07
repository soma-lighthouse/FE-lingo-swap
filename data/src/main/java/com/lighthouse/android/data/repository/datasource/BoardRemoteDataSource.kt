package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.BoardDTO
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow

interface BoardRemoteDataSource {
    fun getBoardQuestions(category: Int, order: String, page: Int): Flow<Resource<BoardDTO>>
    fun uploadQuestion(memberId: Int, categoryId: Int, content: String): Flow<Resource<String>>
    fun updateLike(questionId: Int, memberId: Int): Flow<Resource<String>>
}