package com.lighthouse.board.viewmodel

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.listener.LikeListener
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
) : BaseViewModel(dispatcherProvider, application), LikeListener {
    var categoryId = MutableLiveData(0)
    private var next: MutableList<Int?> = MutableList(7) { null }
    val isLoading = ObservableBoolean()
    val changed = ObservableInt()

    private val _result: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val result: StateFlow<UiState> = _result.asStateFlow()

    private val _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String> = _toast

    private val _questionList: MutableMap<Int, MutableList<BoardQuestionVO>> = mutableMapOf()

    fun getQuestion() = _questionList[categoryId.value!!]

    fun onRefresh() {
        isLoading.set(true)
        categoryId.value?.let {
            next[categoryId.value!!] = null
            _questionList[categoryId.value]?.clear()
            fetchState(null)
        }
    }

    private fun setLike(questionId: Int, like: Boolean) {
        val question = _questionList[categoryId.value]?.find { it.questionId == questionId }
        val num = _questionList[categoryId.value]?.indexOf(question)
        changed.set(-1)
        changed.set(num ?: -1)
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

    fun fetchState(order: String?, pageSize: Int? = null) {
        val category = categoryId.value!!
        if (next[category] == -1) {
            return
        }
        onIO {
            boardRepository.getBoardQuestions(
                category + 1, order, next[category], pageSize
            ).onStart {
                _result.value = UiState.Loading
            }.catch {
                _result.value = handleException(it)
            }.collect {
                if (it.nextId == -1) {
                    next[category] = -1
                } else {
                    next[category] = it.nextId
                }
                val questions = it.questions
                if (_questionList[category] == null) {
                    _questionList[category] = MutableList(1) { BoardQuestionVO() }
                } else {
                    _questionList[category]?.add(BoardQuestionVO())
                }
                _questionList[category]?.addAll(questions)
                isLoading.set(false)
                _result.emit(UiState.Success(_questionList[category] ?: mutableListOf()))
            }
        }
    }

    fun uploadQuestion(upload: UploadQuestionVO) {
        isLoading.set(true)
        if (!isLoading.get()) {
            return
        }
        onIO {
            val text = upload.content
            if (text.length <= 10 || text.length >= 200) {
                val msg =
                    context.getString(com.lighthouse.android.common_ui.R.string.question_size_error)
                _toast.postValue(msg)
            } else {
                boardRepository.uploadQuestion(
                    UploadQuestionVO(
                        1,
                        upload.categoryId,
                        upload.content
                    )
                ).catch {
                    _result.value = handleException(it)
                }.collect {
                    _result.value = UiState.Success(it)
                    isLoading.set(false)
                }
            }
        }
    }


    override fun updateLike(questionId: Int, like: Boolean) {
        if (like) {
            boardRepository.updateLike(questionId)
            setLike(questionId, true)
        } else {
            boardRepository.cancelLike(questionId)
            setLike(questionId, false)
        }
    }

    fun clearResult() {
        _result.value = UiState.Loading
    }
}