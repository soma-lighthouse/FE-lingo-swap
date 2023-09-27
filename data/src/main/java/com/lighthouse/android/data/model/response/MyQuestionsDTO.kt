package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.MyQuestionsVO

data class MyQuestionsDTO(
    @SerializedName("category")
    val categoryId: Int?,
    @SerializedName("questionId")
    val questionId: Int?,
    @SerializedName("contents")
    val contents: String?,
    @SerializedName("likes")
    val likes: Int?,
    @SerializedName("createdAt")
    val createAt: String?,
) {
    fun toVO() = MyQuestionsVO(
        categoryId = categoryId ?: -1,
        questionId = questionId ?: -1,
        contents = contents ?: " ",
        likes = likes ?: -1,
        createAt = createAt ?: " "
    )
}

data class MyQuestionResponse(
    @SerializedName("questions")
    val myQuestionList: List<MyQuestionsDTO>,
)