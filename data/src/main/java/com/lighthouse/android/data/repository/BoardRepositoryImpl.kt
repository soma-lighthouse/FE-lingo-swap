package com.lighthouse.android.data.repository

import com.lighthouse.android.data.repository.datasource.BoardRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.repository.BoardRepository
import com.lighthouse.domain.response.vo.BoardVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor(
    private val datasource: BoardRemoteDataSource,
) : BoardRepository {
    override fun getBoardQuestions(
        category: Int,
        order: String,
        page: Int,
    ): Flow<Resource<BoardVO>> =
        datasource.getBoardQuestions(category, order, page).map {
            when (it) {
                is Resource.Success -> Resource.Success(it.data!!.toVO())
                else -> Resource.Error(it.message ?: "No message found")
            }
        }
}