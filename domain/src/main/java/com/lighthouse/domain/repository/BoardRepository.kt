package com.lighthouse.domain.repository

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.response.vo.BoardVO
import kotlinx.coroutines.flow.Flow

interface BoardRepository {
    fun getBoardQuestions(category: Int, order: String, page: Int): Flow<Resource<BoardVO>>
}