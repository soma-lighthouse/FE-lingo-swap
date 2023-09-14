package com.lighthouse.domain.entity.response.vo

data class MyQuestionsVO(
    val categoryId: Int,
    val questions: List<BoardQuestionVO>,
)