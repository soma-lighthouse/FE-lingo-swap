package com.lighthouse.domain.response.vo

data class ProfileVO(
    val id: Int,
    val description: String,
    val profileImage: String,
    val languages: List<LanguageVO>,
    val name: String,
    val region: String,
    val interests: Map<String, List<String>>,
)