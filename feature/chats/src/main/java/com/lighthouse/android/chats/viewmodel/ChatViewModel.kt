package com.lighthouse.android.chats.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    dispatcherProvider: DispatcherProvider,
    application: Application
) : BaseViewModel(dispatcherProvider, application) {
    private val _question = MutableStateFlow<UiState>(UiState.Loading)
    val question = _question.asStateFlow()

    val sendQuestion: MutableLiveData<String> = MutableLiveData()
    var next: MutableList<Int?> = MutableList(7) { null }
    var page = 1

    fun getQuestion(category: Int) {
        onIO {
            chatRepository.getRecommendedQuestions(category, next[category])
                .onStart {
                    _question.value = UiState.Loading
                }
                .catch {
                    _question.value = handleException(it)
                }.collect {
                    if (it.nextId == -1) {
                        page = -1
                    } else {
                        next[category] = it.nextId
                    }
                    _question.value = UiState.Success(it.questions)
                }
        }
    }
}