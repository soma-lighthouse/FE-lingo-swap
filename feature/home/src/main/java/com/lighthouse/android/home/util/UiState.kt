package com.lighthouse.android.home.util

import com.lighthouse.domain.response.vo.ProfileVO

sealed class UiState {
    object Loading : UiState()
    data class Success(val profiles: List<ProfileVO>) : UiState()
    data class Error(val message: String) : UiState()
}