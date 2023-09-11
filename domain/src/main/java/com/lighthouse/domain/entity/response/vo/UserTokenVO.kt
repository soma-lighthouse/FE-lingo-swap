package com.lighthouse.domain.entity.response.vo

data class UserTokenVO(
    val id: String,
    val accessToken: String,
    val expiresIn: Long,
    val refreshToken: String,
    val refreshTokenExpiresIn: Long,
)