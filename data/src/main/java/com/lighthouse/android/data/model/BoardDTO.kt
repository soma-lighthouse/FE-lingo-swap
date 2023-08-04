package com.lighthouse.android.data.model

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.response.vo.BoardVO

data class BoardDTO(
    @SerializedName("questions")
    val questions: List<BoardQuestionDTO>?,
) {
    fun toVO() = BoardVO(
        questions = questions?.map { it.toVO() } ?: listOf()
    )
}