package com.lighthouse.board.util

import com.lighthouse.domain.response.vo.BoardQuestionVO

sealed class UiState {
    object Loading : UiState()
    data class Success(val profiles: List<BoardQuestionVO>) : UiState()
    data class Error(val message: String) : UiState()
}