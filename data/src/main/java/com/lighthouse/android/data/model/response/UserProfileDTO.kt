package com.lighthouse.android.data.model.response


import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.response.vo.UserProfileVO

data class UserProfileDTO(
    @SerializedName("lastMemberId")
    val nextId: Int?,
    @SerializedName("profiles")
    val profiles: List<ProfileDTO>?,
) {
    fun toVO() = UserProfileVO(
        nextId = nextId ?: -1,
        profile = profiles?.map { it.toVO() } ?: listOf()
    )
}