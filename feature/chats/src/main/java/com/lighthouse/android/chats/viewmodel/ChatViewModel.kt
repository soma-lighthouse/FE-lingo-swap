package com.lighthouse.android.chats.viewmodel

import androidx.lifecycle.MutableLiveData
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.LighthouseException
import com.lighthouse.domain.usecase.ManageChannelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCase: ManageChannelUseCase,
    dispatcherProvider: DispatcherProvider
) : BaseViewModel(dispatcherProvider) {
    private val _question = MutableStateFlow<UiState>(UiState.Loading)
    val question = _question.asStateFlow()

    val sendQuestion: MutableLiveData<String> = MutableLiveData()
    var next: MutableList<Int?> = MutableList(7) { null }
    var page = 1

    fun getQuestion(category: Int) {
        onIO {
            chatUseCase.getRecommendedQuestions(category, next[category])
                .catch {
                    if (it is LighthouseException) {
                        _question.value = UiState.Error(it)
                    }
                }.collect {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data!!.nextId == -1) {
                                page = -1
                            } else {
                                next[category] = it.data!!.nextId
                            }
                            _question.value = UiState.Success(it.data!!.questions)
                        }

                        is Resource.Error ->
                            _question.value = UiState.Error(it.message ?: "Error found")
                    }
                }
        }

    }


}