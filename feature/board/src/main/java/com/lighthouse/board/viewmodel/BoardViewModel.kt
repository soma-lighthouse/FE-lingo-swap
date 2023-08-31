package com.lighthouse.board.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.common_ui.util.StringSet
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.UploadQuestionVO
import com.lighthouse.domain.entity.response.vo.BoardQuestionVO
import com.lighthouse.domain.usecase.GetQuestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val getQuestionUseCase: GetQuestionUseCase,
) : ViewModel() {
    private var next: Int? = null
    private var questions = listOf<BoardQuestionVO>()
    var page = 1

    fun fetchState(category: Int, order: String?, pageSize: Int? = null): Flow<UiState> {
        return getQuestionUseCase.invoke(category + 1, order, next)
            .map {
                when (it) {
                    is Resource.Success -> {
                        if (it.data!!.nextId == -1) {
                            page = -1
                        } else {
                            next = it.data!!.nextId
                        }
                        UiState.Success(it.data!!.questions)
                    }

                    is Resource.Error -> UiState.Error(it.message ?: StringSet.error_msg)
                }
            }
            .catch {
                emit(UiState.Error(it.message ?: StringSet.error_msg))
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = UiState.Loading,
                started = SharingStarted.WhileSubscribed(5000)
            )
    }

    fun uploadQuestion(info: UploadQuestionVO) =
        getQuestionUseCase.uploadQuestion(info)
            .map {
                when (it) {
                    is Resource.Success -> UiState.Success(it.data!!)
                    else -> UiState.Error(it.message ?: StringSet.error_msg)
                }
            }
            .catch {
                emit(UiState.Error(it.message ?: StringSet.error_msg))
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = UiState.Loading,
                started = SharingStarted.WhileSubscribed(5000)
            )

    fun updateLike(questionId: Int, userId: String) {
        getQuestionUseCase.updateLike(questionId, userId)
    }

    fun cancelLike(questionId: Int, userId: String) {
        getQuestionUseCase.cancelLike(questionId, userId)
    }

    fun saveQuestion(questions: List<BoardQuestionVO>) {
        this.questions = questions
    }

}