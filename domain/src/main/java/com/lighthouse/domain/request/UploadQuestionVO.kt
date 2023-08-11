package com.lighthouse.domain.request

data class UploadQuestionVO(
    val userId: Int,
    val categoryId: Int,
    val content: String,
)