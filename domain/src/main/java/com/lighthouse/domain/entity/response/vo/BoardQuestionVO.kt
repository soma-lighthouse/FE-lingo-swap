package com.lighthouse.domain.entity.response.vo


data class BoardQuestionVO(
    val questionId: Int,
    val memberId: Int,
    val categoryId: Int,
    val profileImage: String,
    val name: String,
    val region: String,
    val contents: String,
    val like: Int,
)