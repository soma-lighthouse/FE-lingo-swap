package com.lighthouse.domain.response.dto

data class UserProfileVO(
    val nextId: Int,
    val profile: List<ProfileVO>,
)