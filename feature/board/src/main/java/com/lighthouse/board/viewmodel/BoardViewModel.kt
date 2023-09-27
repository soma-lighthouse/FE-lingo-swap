package com.lighthouse.board.viewmodel

import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.StringSet
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.UploadQuestionVO
import com.lighthouse.domain.entity.response.vo.BoardQuestionVO
import com.lighthouse.domain.usecase.GetQuestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val getQuestionUseCase: GetQuestionUseCase,
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel(dispatcherProvider) {
    var next: MutableList<Int?> = MutableList(7) { null }
    private var questions = listOf<BoardQuestionVO>()
    var page = 1

    private val _result: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val result: StateFlow<UiState> = _result.asStateFlow()

    fun fetchState(category: Int, order: String?, pageSize: Int? = null) {
        onIO {
            getQuestionUseCase.invoke(
                category + 1, order, next[category], pageSize
            ).onStart {
                _result.value = UiState.Loading
            }.catch {
                _result.value = handleException(it)
            }.collect {
                when (it) {
                    is Resource.Success -> {
                        if (it.data!!.nextId == -1) {
                            page = -1
                        } else {
                            next[category] = it.data!!.nextId
                        }
                        _result.value = UiState.Success(it.data!!.questions)
                    }

                    is Resource.Error -> _result.value =
                        UiState.Error(it.message ?: StringSet.error_msg)
                }
            }

        }
    }

    fun uploadQuestion(info: UploadQuestionVO) {
        onIO {
            getQuestionUseCase.uploadQuestion(info).catch {
                _result.value = handleException(it)
            }.collect {
                when (it) {
                    is Resource.Success -> _result.value = UiState.Success(it.data!!)
                    else -> _result.value = UiState.Error(it.message ?: StringSet.error_msg)
                }
            }
        }
    }

    fun updateLike(questionId: Int) {
        getQuestionUseCase.updateLike(questionId)
    }

    fun cancelLike(questionId: Int) {
        getQuestionUseCase.cancelLike(questionId)
    }

    fun clearResult() {
        _result.value = UiState.Loading
    }

    fun saveQuestion(questions: List<BoardQuestionVO>) {
        this.questions = questions
    }

    fun getQuestions() = questions

}