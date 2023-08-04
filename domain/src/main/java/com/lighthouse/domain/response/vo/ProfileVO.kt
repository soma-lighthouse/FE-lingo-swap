package com.lighthouse.domain.response.vo

data class ProfileVO(
    val id: Int,
    val description: String,
    val profileImage: String,
    val language: List<LanguageVO>,
    val name: String,
    val region: String,
)