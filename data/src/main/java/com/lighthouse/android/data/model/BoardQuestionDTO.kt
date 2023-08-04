package com.lighthouse.android.data.model

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.response.vo.BoardQuestionVO

data class BoardQuestionDTO(
    @SerializedName("questionId")
    val questionId: Int?,
    @SerializedName("userId")
    val userId: Int?,
    @SerializedName("categoryId")
    val categoryId: Int?,
    @SerializedName("contents")
    val contents: String?,
    @SerializedName("like")
    val like: Int?,
) {
    fun toVO() = BoardQuestionVO(
        questionId = questionId ?: -1,
        userId = userId ?: -1,
        categoryId = categoryId ?: -1,
        contents = contents ?: " ",
        like = like ?: -1
    )

}