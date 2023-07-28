package com.lighthouse.domain.response.dto

data class UserProfileVO(
    val page: Int,
    val profile: List<ProfileVO>,
)