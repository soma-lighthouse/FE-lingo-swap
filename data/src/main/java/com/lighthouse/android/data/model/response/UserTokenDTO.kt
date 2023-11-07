package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.TokenVO
import com.lighthouse.domain.entity.response.vo.UserTokenVO

data class UserTokenDTO(
    @SerializedName("uuid")
    val uuid: String?,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("tokens")
    val tokens: TokenDTO?,

    ) {
    fun toVO() = UserTokenVO(
        uuid = uuid ?: "",
        tokens = tokens?.toVO() ?: TokenVO("", -1, "", -1),
        userName = userName ?: ""
    )
}

data class TokenDTO(
    @SerializedName("accessToken")
    val accessToken: String?,
    @SerializedName("expiresIn")
    val expiresIn: Long?,
    @SerializedName("refreshToken")
    val refreshToken: String?,
    @SerializedName("refreshTokenExpiresIn")
    val refreshTokenExpiresIn: Long?,
) {
    fun toVO() = TokenVO(
        accessToken = accessToken ?: "",
        expiresIn = expiresIn ?: -1,
        refreshToken = refreshToken ?: "",
        refreshTokenExpiresIn = refreshTokenExpiresIn ?: -1
    )
}