package com.lighthouse.android.data.repository

import com.lighthouse.android.data.model.request.UpdateLikeDTO
import com.lighthouse.android.data.model.request.UploadQuestionDTO
import com.lighthouse.android.data.repository.datasource.BoardRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.repository.BoardRepository
import com.lighthouse.domain.request.UpdateLikeVO
import com.lighthouse.domain.request.UploadQuestionVO
import com.lighthouse.domain.response.vo.BoardVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor(
    private val datasource: BoardRemoteDataSource,
) : BoardRepository {
    override fun getBoardQuestions(
        category: Int,
        order: String?,
        page: Int,
    ): Flow<Resource<BoardVO>> =
        datasource.getBoardQuestions(category, order, page).map {
            when (it) {
                is Resource.Success -> Resource.Success(it.data!!.toVO())
                else -> Resource.Error(it.message ?: "No message found")
            }
        }

    override fun uploadQuestion(
        info: UploadQuestionVO,
    ): Flow<Resource<String>> =
        datasource.uploadQuestion(
            UploadQuestionDTO(
                userId = info.userId,
                categoryId = info.categoryId,
                content = info.content
            )
        ).map {
            when (it) {
                is Resource.Success -> Resource.Success(it.data!!)
                else -> Resource.Error(it.message ?: "No message Found")
            }
        }

    override fun updateLike(questionId: Int, memberId: UpdateLikeVO): Flow<Resource<String>> =
        datasource.updateLike(questionId, UpdateLikeDTO(memberId.memberId)).map {
            when (it) {
                is Resource.Success -> Resource.Success(it.data!!)
                else -> Resource.Error(it.message ?: "No message Found")
            }
        }
}