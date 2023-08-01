package com.lighthouse.android.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.home.util.UiState
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.usecase.GetMatchedUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMatchedUserUseCase: GetMatchedUserUseCase,
) : ViewModel() {
    val page: MutableStateFlow<Int> = MutableStateFlow(1)
    private var cachedState: StateFlow<UiState>? = null


    val state: Flow<UiState>
        get() {
            if (cachedState == null) {
                cachedState = fetchState()
            }
            return cachedState!!
        }

    private fun fetchState(): StateFlow<UiState> {
        return getMatchedUserUseCase.invoke(page.value)
            .map {
                Log.d("TESTING", it.toString())
                when (it) {
                    is Resource.Success -> {
                        if (it.data!!.page == -1) {
                            page.value = -1
                        }
                        UiState.Success(it.data!!.profile)
                    }

                    is Resource.Error -> UiState.Error(it.message ?: "Error Found")
                }
            }
            .catch {
                Log.d("ERROR", it.message.toString())
                emit(UiState.Error(it.message ?: "Error Found"))
            }
            .onCompletion {
                Log.d("Complete", "Finish getting User Profile")
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = UiState.Loading,
                started = SharingStarted.WhileSubscribed(5000)
            )
    }

}