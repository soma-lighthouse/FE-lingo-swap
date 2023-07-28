package com.lighthouse.android.home.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
) : ViewModel() {


//    val homeData: Flow<UiState> = drivenRepository
//        .getDriven()
//        .map {
//            UiState.Success(it) as UiState
//        }
//        .onCompletion {
//        }.stateIn(
//            scope = viewModelScope,
//            initialValue = UiState.Loading,
//            started = SharingStarted.WhileSubscribed(5000)
//        )
}