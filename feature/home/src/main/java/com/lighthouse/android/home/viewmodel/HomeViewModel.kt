package com.lighthouse.android.home.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.home.util.UiState
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.response.dto.ProfileVO
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
    private val _page: MutableStateFlow<Int> = MutableStateFlow(1)
    val page: StateFlow<Int> = _page
    private var cachedState: StateFlow<UiState>? = null
    private var userProfiles = listOf<ProfileVO>()
    var loading = MutableLiveData<Boolean>()

    val state: Flow<UiState>
        get() {
            if (cachedState == null) {
                cachedState = fetchState()
            }
            return cachedState!!
        }

    private fun fetchState(): StateFlow<UiState> {
        _page.value = 1
        return getMatchedUserUseCase.invoke(_page.value)
            .map {
                when (it) {
                    is Resource.Success -> {
                        Log.d("TESTING", it.data!!.nextId.toString())
                        if (it.data!!.nextId == -1) {
                            _page.value = -1
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

    fun fetchNextPage(): Flow<List<ProfileVO>?> {
        _page.value++
        return getMatchedUserUseCase.invoke(_page.value)
            .map {
                when (it) {
                    is Resource.Success -> {
                        Log.d("TESTING", it.data!!.nextId.toString())
                        if (it.data!!.nextId == -1) {
                            Log.d("PAGING", "enter")
                            _page.value = -1
                        }
                        it.data!!.profile
                    }

                    is Resource.Error -> {
                        Log.e("PAGING", "Paging error occurred ")
                        null
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = null,
                started = SharingStarted.WhileSubscribed(5000)
            )
    }

    fun saveUserProfiles(profiles: List<ProfileVO>) {
        userProfiles = profiles
    }

    fun getUserProfiles() = userProfiles

}