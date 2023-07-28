package com.lighthouse.android.data.model


import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.response.dto.UserProfileVO

data class UserProfileDTO(
    @SerializedName("page")
    val page: Int?,
    @SerializedName("profiles")
    val profiles: List<ProfileDTO>?,
) {
    fun toVO() = UserProfileVO(
        page = page ?: 0,
        profile = profiles?.map { it.toVO() } ?: listOf()
    )
}