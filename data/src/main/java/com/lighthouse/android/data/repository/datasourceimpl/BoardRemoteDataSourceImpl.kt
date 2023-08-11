package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.BoardApiService
import com.lighthouse.android.data.model.request.UpdateLikeDTO
import com.lighthouse.android.data.model.request.UploadQuestionDTO
import com.lighthouse.android.data.model.response.BoardDTO
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
        order: String?,
        page: Int,
    ): Flow<Resource<BoardDTO>> = flow {
        emit(changeResult(api.getQuestion(category, order, page)))
    }

    override fun uploadQuestion(
        info: UploadQuestionDTO,
    ): Flow<Resource<String>> = flow {
        emit(changeResult(api.uploadQuestion(info)))
    }

    override fun updateLike(questionId: Int, memberId: UpdateLikeDTO): Flow<Resource<String>> =
        flow {
            emit(changeResult(api.updateLike(questionId, memberId)))
        }
}