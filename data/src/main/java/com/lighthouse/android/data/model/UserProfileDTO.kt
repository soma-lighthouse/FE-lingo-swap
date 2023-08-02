package com.lighthouse.android.data.model


import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.response.dto.UserProfileVO

data class UserProfileDTO(
    @SerializedName("nextId")
    val nextId: Int?,
    @SerializedName("profiles")
    val profiles: List<ProfileDTO>?,
) {
    fun toVO() = UserProfileVO(
        nextId = nextId ?: -1,
        profile = profiles?.map { it.toVO() } ?: listOf()
    )
}