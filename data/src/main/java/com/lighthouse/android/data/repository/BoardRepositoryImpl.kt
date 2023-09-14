package com.lighthouse.android.data.repository

import android.util.Log
import com.lighthouse.android.data.model.request.UploadQuestionDTO
import com.lighthouse.android.data.repository.datasource.BoardRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.UploadQuestionVO
import com.lighthouse.domain.entity.response.vo.BoardVO
import com.lighthouse.domain.repository.BoardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor(
    private val datasource: BoardRemoteDataSource,
) : BoardRepository {
    override fun getBoardQuestions(
        category: Int,
        order: String?,
        page: Int?,
        pageSize: Int?,
    ): Flow<Resource<BoardVO>> =
        datasource.getBoardQuestions(category, order, page, pageSize).map {
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

    override fun updateLike(questionId: Int, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datasource.updateLike(questionId, mapOf("userId" to userId)).collect {
                when (it) {
                    is Resource.Success ->
                        Log.d("LIKE", "Success")

                    else -> Log.d("LIKE", it.message.toString())
                }
            }
        }
    }

    override fun cancelLike(questionId: Int, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datasource.cancelLike(questionId).collect {
                when (it) {
                    is Resource.Success ->
                        Log.d("LIKE", "Success")

                    else -> Log.d("LIKE", it.message.toString())
                }
            }
        }
    }

}