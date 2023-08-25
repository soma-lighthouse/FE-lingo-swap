package com.lighthouse.domain.entity.response.vo

data class ProfileVO(
    val id: String,
    val description: String,
    val profileImageUri: String,
    val languages: List<LanguageVO>,
    val name: String,
    val region: String,
    val countries: List<String>,
    val interests: List<InterestVO>,
)