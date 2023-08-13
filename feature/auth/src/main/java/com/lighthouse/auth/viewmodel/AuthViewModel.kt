package com.lighthouse.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.common_ui.util.StringSet
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.usecase.GetAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val useCase: GetAuthUseCase,
) : ViewModel() {
    fun getUUID() = useCase.getUserId()

    fun getInterestList() = useCase.getInterestList()
        .map {
            when (it) {
                is Resource.Success -> UiState.Success(it)
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
}