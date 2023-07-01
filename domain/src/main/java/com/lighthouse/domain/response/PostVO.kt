package com.lighthouse.domain.response

data class PostVO(
    val body: String,
    val id: Int,
    val title: String,
    val userId: Int
)