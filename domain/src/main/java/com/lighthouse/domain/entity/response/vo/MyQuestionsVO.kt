package com.lighthouse.domain.entity.response.vo

data class MyQuestionsVO(
    val categoryId: Int,
    val questionId: Int,
    val contents: String,
    val likes: Int,
    val createAt: String
)