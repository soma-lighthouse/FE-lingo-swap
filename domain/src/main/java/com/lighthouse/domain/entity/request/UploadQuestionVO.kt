package com.lighthouse.domain.entity.request

data class UploadQuestionVO(
    val uuid: Int,
    val categoryId: Int,
    val content: String,
)