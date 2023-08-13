package com.lighthouse.domain.entity.response.vo

data class ProfileVO(
    val id: Int,
    val description: String,
    val profileImage: String,
    val languages: List<LanguageVO>,
    val name: String,
    val region: String,
    val countries: List<String>,
    val interests: List<InterestVO>,
)