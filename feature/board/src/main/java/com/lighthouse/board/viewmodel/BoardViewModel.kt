package com.lighthouse.board.viewmodel

import android.app.Application
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.domain.entity.request.UploadQuestionVO
import com.lighthouse.domain.entity.response.vo.BoardQuestionVO
import com.lighthouse.domain.repository.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    dispatcherProvider: DispatcherProvider,
    application: Application
) : BaseViewModel(dispatcherProvider, application) {
    var next: MutableList<Int?> = MutableList(7) { null }
    var page = 1

    private val _result: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val result: StateFlow<UiState> = _result.asStateFlow()

    private val _questionList: MutableMap<Int, List<BoardQuestionVO>> = mutableMapOf()
    private val _firstNext: MutableList<Int?> = MutableList(7) { null }

    fun getQuestion(category: Int) = _questionList[category]
    fun getFirstNext(category: Int) = _firstNext[category]

    fun setLike(category: Int, questionId: Int, like: Boolean) {

        val question = _questionList[category]?.find { it.questionId == questionId }

        question?.let {
            if (like) {
                it.like += 1
                it.clicked = true
            } else {
                it.like -= 1
                it.clicked = false
            }
        }
    }

    fun fetchState(category: Int, order: String?, pageSize: Int? = null) {
        onIO {
            boardRepository.getBoardQuestions(
                category + 1, order, next[category], pageSize
            ).onStart {
                _result.value = UiState.Loading
            }.catch {
                _result.value = handleException(it)
            }.collect {
                if (it.nextId == -1) {
                    page = -1
                } else {
                    next[category] = it.nextId
                }
                val questions = it.questions
                if (_questionList[category] == null) {
                    _questionList[category] = questions
                    _firstNext[category] = it.nextId
                }
                _result.value = UiState.Success(questions)
            }
        }

    }

    fun uploadQuestion(info: UploadQuestionVO) {
        onIO {
            boardRepository.uploadQuestion(info).catch {
                _result.value = handleException(it)
            }.collect {
                _result.value = UiState.Success(it)
            }
        }
    }

    fun updateLike(questionId: Int) {
        boardRepository.updateLike(questionId)
    }

    fun cancelLike(questionId: Int) {
        boardRepository.cancelLike(questionId)
    }

    fun clearResult() {
        _result.value = UiState.Loading
    }
}