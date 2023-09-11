package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.LighthouseException
import com.lighthouse.domain.entity.response.vo.UserTokenVO

data class UserTokenDTO(
    @SerializedName("id")
    val id: String?,
    @SerializedName("accessToken")
    val accessToken: String?,
    @SerializedName("expiresIn")
    val expiresIn: Long?,
    @SerializedName("refreshToken")
    val refreshToken: String?,
    @SerializedName("refreshTokenExpiresIn")
    val refreshTokenExpiresIn: Long?,
) {
    fun toVO() = UserTokenVO(
        id = id ?: "",
        accessToken = accessToken ?: throw LighthouseException(null, null),
        expiresIn = expiresIn ?: -1,
        refreshToken = refreshToken ?: throw LighthouseException(null, null),
        refreshTokenExpiresIn = refreshTokenExpiresIn ?: -1
    )
}