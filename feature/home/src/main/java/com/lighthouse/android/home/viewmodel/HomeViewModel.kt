package com.lighthouse.android.home.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.response.vo.ProfileVO
import com.lighthouse.domain.usecase.GetMatchedUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMatchedUserUseCase: GetMatchedUserUseCase,
) : ViewModel() {
    private val _page: MutableStateFlow<Int> = MutableStateFlow(1)
    val page: StateFlow<Int>
        get() = _page
    private var userProfiles = listOf<ProfileVO>()
    var loading = MutableLiveData<Boolean>()
    private var next: Int? = null


    fun fetchNextPage(
        userId: Int,
        pageSize: Int?,
    ): Flow<UiState> {
        return getMatchedUserUseCase.invoke(userId, next, pageSize)
            .map {
                when (it) {
                    is Resource.Success -> {
                        if (it.data!!.nextId == -1) {
                            Log.d("PAGING", "enter")
                            _page.value = -1
                        } else {
                            next = it.data!!.nextId
                        }
                        UiState.Success(it.data!!.profile)
                    }

                    is Resource.Error -> UiState.Error(it.message ?: "Error Occurred")
                }
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = UiState.Loading,
                started = SharingStarted.WhileSubscribed(5000)
            )
    }

    fun saveUserProfiles(profiles: List<ProfileVO>) {
        userProfiles = profiles
    }

    fun getUserProfiles() = userProfiles

}