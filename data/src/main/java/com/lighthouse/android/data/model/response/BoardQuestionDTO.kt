package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.response.vo.BoardQuestionVO

data class BoardQuestionDTO(
    @SerializedName("questionId")
    val questionId: Int?,
    @SerializedName("userId")
    val memberId: Int?,
    @SerializedName("categoryId")
    val categoryId: Int?,
    @SerializedName("profileImage")
    val profileImage: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("region")
    val region: String?,
    @SerializedName("contents")
    val contents: String?,
    @SerializedName("likes")
    val like: Int?,
) {
    fun toVO() = BoardQuestionVO(
        questionId = questionId ?: -1,
        memberId = memberId ?: -1,
        categoryId = categoryId ?: -1,
        contents = contents ?: " ",
        profileImage = profileImage ?: " ",
        name = name ?: " ",
        region = region ?: " ",
        like = like ?: -1
    )

}