package com.lighthouse.board.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.board.util.UiState
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.usecase.GetQuestionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class BoardViewModel @Inject constructor(
    private val getQuestionUseCase: GetQuestionUseCase,
) : ViewModel() {
    private var cachedState: StateFlow<UiState>? = null
    val category = MutableLiveData<Int>()
    val order = MutableLiveData<String>()

    val state: Flow<UiState>
        get() {
            if (cachedState == null) {
                cachedState = fetchState()
            }
            return cachedState!!
        }

    private fun fetchState(): StateFlow<UiState> {
        return getQuestionUseCase.invoke(category.value!!, order.value!!, 1)
            .map {
                when (it) {
                    is Resource.Success -> UiState.Success(it.data!!.questions)
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
}