package com.lighthouse.android.data.repository

import android.util.Log
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.model.request.UploadQuestionDTO
import com.lighthouse.android.data.repository.datasource.BoardRemoteDataSource
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
    private val localPreferenceDataSource: LocalPreferenceDataSource
) : BoardRepository {
    override fun getBoardQuestions(
        category: Int,
        order: String?,
        page: Int?,
        pageSize: Int?,
    ): Flow<BoardVO> =
        datasource.getBoardQuestions(category, order, page, pageSize).map {
            it.toVO()
        }

    override fun uploadQuestion(
        info: UploadQuestionVO,
    ): Flow<Boolean> =
        datasource.uploadQuestion(
            UploadQuestionDTO(
                uuid = localPreferenceDataSource.getUUID().toString(),
                categoryId = info.categoryId,
                content = info.content
            )
        )

    override fun updateLike(questionId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            datasource.updateLike(questionId).collect {
                Log.d("LIKE", "Success")
            }
        }

    }

    override fun cancelLike(questionId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            datasource.cancelLike(questionId).collect {
                Log.d("LIKE", "Success")
            }
        }
    }
}
