package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.BoardApiService
import com.lighthouse.android.data.model.request.UploadQuestionDTO
import com.lighthouse.android.data.model.response.BoardDTO
import com.lighthouse.android.data.repository.datasource.BoardRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BoardRemoteDataSourceImpl @Inject constructor(
    private val api: BoardApiService,
) : BoardRemoteDataSource, NetworkResponse() {
    override fun getBoardQuestions(
        category: Int,
        order: String?,
        page: Int?,
        pageSize: Int?,
    ): Flow<BoardDTO> = flow {
        emit(changeResult(api.getQuestion(category, order, page, pageSize)))
    }

    override fun uploadQuestion(
        info: UploadQuestionDTO,
    ): Flow<Boolean> = flow {
        val response = api.uploadQuestion(info)
        if (response.isSuccessful) {
            emit(true)
        } else {
            throw errorHandler(response)
        }
    }

    override fun updateLike(questionId: Int): Flow<Boolean> =
        flow {
            val response = api.updateLike(questionId)
            if (response.isSuccessful) {
                emit(true)
            } else {
                throw errorHandler(response)
            }
        }

    override fun cancelLike(questionId: Int): Flow<Boolean> =
        flow {
            val response = api.cancelLike(questionId)
            if (response.isSuccessful) {
                emit(true)
            } else {
                throw errorHandler(response)
            }
        }

}