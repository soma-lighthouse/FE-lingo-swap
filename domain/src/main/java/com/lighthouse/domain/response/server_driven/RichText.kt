package com.lighthouse.domain.response.server_driven


data class RichText(
    val textRichType: RichTextType?,
    val imageRichType: RichImageType?,
)


data class RichTextType(
    val text: String,
    val style: List<String>?,
    val textColor: String?,
    val background: String?,
    val size: Float?,
)

data class RichImageType(
    val url: String,
    val width: Float?,
    val height: Float?,
)
