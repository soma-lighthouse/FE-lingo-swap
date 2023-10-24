package com.lighthouse.domain.entity.request

data class UploadQuestionVO(
    val uuid: Int,
    var categoryId: Int,
    var content: String,
)