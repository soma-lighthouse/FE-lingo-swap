package com.lighthouse.android.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.common_ui.util.StringSet
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.domain.usecase.GetMatchedUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMatchedUserUseCase: GetMatchedUserUseCase,
) : ViewModel() {
    private var userProfiles = listOf<ProfileVO>()
    private var next: Int? = null

    private val _matchedUserUiState = MutableStateFlow<UiState>(UiState.Loading)
    val matchedUserUiState: StateFlow<UiState> = _matchedUserUiState.asStateFlow()

    var page = 1
    fun fetchNextPage(
        userId: String,
        pageSize: Int? = null,
    ) {
        viewModelScope.launch {
            getMatchedUserUseCase.invoke(userId, next, pageSize)
                .catch {
                    _matchedUserUiState.value = UiState.Error(it.message ?: StringSet.error_msg)
                }
                .collect {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data!!.nextId == -1) {
                                Log.d("PAGING", "enter")
                                page = -1
                            } else {
                                next = it.data!!.nextId
                            }
                            _matchedUserUiState.emit(UiState.Success(it.data!!.profile))
                        }

                        is Resource.Error -> _matchedUserUiState.value =
                            UiState.Error(it.message ?: StringSet.error_msg)
                    }
                }
        }
    }

    fun saveUserProfiles(profiles: List<ProfileVO>) {
        userProfiles = profiles
    }

    fun getUserProfiles() = userProfiles

}