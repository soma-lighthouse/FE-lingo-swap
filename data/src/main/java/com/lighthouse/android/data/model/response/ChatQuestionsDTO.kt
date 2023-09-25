package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.ChatQuestionsVO

data class ChatQuestionsDTO(
    @SerializedName("nextId")
    val nextId: Int?,
    @SerializedName("questions")
    val questions: List<String>?
) {
    fun toVO() = ChatQuestionsVO(
        nextId = nextId ?: -1,
        questions = questions ?: listOf()
    )
}
