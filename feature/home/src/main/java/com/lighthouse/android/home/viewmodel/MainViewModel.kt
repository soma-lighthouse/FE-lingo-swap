package com.lighthouse.android.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.lighthouse.android.home.util.UiState
import com.lighthouse.domain.repository.DrivenRepository
import com.lighthouse.domain.response.dto.ProfileVO
import com.lighthouse.domain.usecase.GetMatchedUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMatchedUserUseCase: GetMatchedUserUseCase,
    drivenRepository: DrivenRepository,
) : ViewModel() {
    val pagingDataFlow: Flow<PagingData<ProfileVO>>
    val state: StateFlow<UiState> = MutableStateFlow(UiState.Loading)

    init {
        pagingDataFlow = getUser()
        viewModelScope.launch {
            state.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState.Loading
            )
        }
    }

    private fun getUser(): Flow<PagingData<ProfileVO>> =
        getMatchedUserUseCase.invoke()

    val drivenData: Flow<UiState> = drivenRepository
        .getDriven()
        .map {
            UiState.Success(it) as UiState
        }
        .onCompletion {
        }.stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(5000)
        )
}