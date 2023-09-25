package com.lighthouse.domain.entity.request

data class UploadInterestVO(
    val category: String,
    val interests: List<String>
)