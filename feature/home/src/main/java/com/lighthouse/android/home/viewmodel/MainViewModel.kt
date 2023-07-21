package com.lighthouse.android.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.home.util.UiState
import com.lighthouse.domain.repository.DrivenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val drivenRepository: DrivenRepository
) : ViewModel() {

    val dataDriven: Flow<UiState> = drivenRepository
        .getDriven()
        .map {
            UiState.Success(it) as UiState
        }
        .onStart {
            emit(UiState.Loading)
        }
        .onCompletion {
            Log.d("TEST", it.toString())
        }.shareIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            1
        )
}