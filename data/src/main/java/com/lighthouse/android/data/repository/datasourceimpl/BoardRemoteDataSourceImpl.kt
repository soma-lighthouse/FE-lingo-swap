package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.BoardApiService
import com.lighthouse.android.data.model.BoardDTO
import com.lighthouse.android.data.repository.datasource.BoardRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BoardRemoteDataSourceImpl @Inject constructor(
    private val api: BoardApiService,
) : BoardRemoteDataSource, NetworkResponse() {
    override fun getBoardQuestions(
        category: Int,
        order: String,
        page: Int,
    ): Flow<Resource<BoardDTO>> = flow {
        emit(changeResult(api.getQuestion(category, order, page)))
    }
}