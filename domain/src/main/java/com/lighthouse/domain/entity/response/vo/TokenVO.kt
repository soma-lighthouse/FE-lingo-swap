package com.lighthouse.domain.entity.response.vo

data class TokenVO(
    val accessToken: String,
    val expiresIn: Long,
    val refreshToken: String,
    val refreshTokenExpiresIn: Long,
)
