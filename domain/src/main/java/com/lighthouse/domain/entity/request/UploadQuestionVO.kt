package com.lighthouse.domain.entity.request

data class UploadQuestionVO(
    val userId: Int,
    val categoryId: Int,
    val content: String,
)