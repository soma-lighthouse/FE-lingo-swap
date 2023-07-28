package com.lighthouse.android.home.util

import com.lighthouse.domain.response.server_driven.ViewTypeVO

sealed class UiState {
    object Loading : UiState()
    data class Success(val drivenData: List<ViewTypeVO>) : UiState()
    data class Error(val message: String) : UiState()
}