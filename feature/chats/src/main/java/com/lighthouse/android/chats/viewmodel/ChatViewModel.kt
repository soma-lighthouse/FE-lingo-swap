package com.lighthouse.android.chats.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.usecase.GetQuestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val useCase: GetQuestionUseCase,
) : ViewModel() {
    val question: MutableLiveData<String> = MutableLiveData()
    var next: MutableList<Int?> = MutableList(7) { null }
    var page = 1

    fun getQuestion(category: Int, order: String?, pageSize: Int? = null) =
        useCase.invoke(category, order, next[category], pageSize)
            .map {
                when (it) {
                    is Resource.Success -> {
                        if (it.data!!.nextId == -1) {
                            page = -1
                        } else {
                            next[category] = it.data!!.nextId
                        }
                        UiState.Success(it.data!!.questions)
                    }

                    is Resource.Error -> UiState.Error(it.message ?: "Error found")
                }
            }
            .catch {
                emit(UiState.Error(it.message ?: "Error found"))
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = UiState.Loading,
                started = SharingStarted.WhileSubscribed(5000)
            )


}