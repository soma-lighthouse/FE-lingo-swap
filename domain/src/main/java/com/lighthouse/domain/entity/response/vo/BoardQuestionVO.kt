package com.lighthouse.domain.entity.response.vo


data class BoardQuestionVO(
    val questionId: Int,
    val userId: String,
    val categoryId: Int,
    val profileImageUri: String,
    val name: String,
    val region: String,
    val contents: String,
    val like: Int,
)