package com.lighthouse.domain.entity.response.vo

data class UserProfileVO(
    val nextId: Int,
    val profile: List<MatchProfileVO>,
)