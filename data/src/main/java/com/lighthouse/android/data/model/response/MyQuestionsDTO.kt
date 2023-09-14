package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.MyQuestionsVO

data class MyQuestionsDTO(
    @SerializedName("category")
    val categoryId: Int?,
    @SerializedName("myQuestions")
    val questions: List<BoardQuestionDTO>?,
) {
    fun toVO() = MyQuestionsVO(
        categoryId = categoryId ?: -1,
        questions = questions?.map { it.toVO() } ?: listOf()
    )
}

data class MyQuestionResponse(
    @SerializedName("myQuestionList")
    val myQuestionList: List<MyQuestionsDTO>,
)