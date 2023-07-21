package com.lighthouse.domain.response

data class RichText(
    val text: String?,
    val style: List<String>?,
    val textColor: String?,
    val background: String?,
    val size: Float?,
    val url: String?,
    val width: Float?,
    val height: Float?
)
