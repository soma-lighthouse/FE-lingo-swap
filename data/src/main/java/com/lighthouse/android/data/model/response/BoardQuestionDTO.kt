package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.BoardQuestionVO

data class BoardQuestionDTO(
    @SerializedName("questionId")
    val questionId: Int?,
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("categoryId")
    val categoryId: Int?,
    @SerializedName("profileImageUri")
    val profileImageUri: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("region")
    val region: String?,
    @SerializedName("contents")
    val contents: String?,
    @SerializedName("likes")
    val like: Int?,
    val statusCode: Int,

    ) {
    fun toVO() = BoardQuestionVO(
        questionId = questionId ?: -1,
        userId = userId ?: "",
        categoryId = categoryId ?: -1,
        contents = contents ?: " ",
        profileImageUri = profileImageUri ?: " ",
        name = name ?: " ",
        region = region ?: " ",
        like = like ?: -1
    )

}