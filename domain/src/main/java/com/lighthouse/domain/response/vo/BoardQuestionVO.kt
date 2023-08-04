package com.lighthouse.domain.response.vo

data class BoardQuestionVO(
    val questionId: Int,
    val userId: Int,
    val categoryId: Int,
    val contents: String,
    val like: Int,
)