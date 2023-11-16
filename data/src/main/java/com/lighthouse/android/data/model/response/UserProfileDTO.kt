package com.lighthouse.android.data.model.response


import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.MatchProfileVO
import com.lighthouse.domain.entity.response.vo.UserProfileVO

data class UserProfileDTO(
    @SerializedName("nextId")
    val nextId: Int?,
    @SerializedName("profiles")
    val profiles: List<MatchProfileDTO>?,
) {
    fun toVO() = UserProfileVO(
        nextId = nextId ?: -1,
        profile = profiles?.map { it.toVO() } ?: listOf()
    )
}

data class MatchProfileDTO(
    @SerializedName("uuid")
    val id: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("profileImageUri")
    val profileImageUri: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("region")
    val region: String?,
    @SerializedName("preferredInterests")
    val interests: List<String>?,
) {
    fun toVO() = MatchProfileVO(
        id = id ?: "",
        description = description ?: "",
        profileImageUri = profileImageUri ?: "",
        name = name ?: "",
        region = region ?: "",
        interests = interests ?: listOf()

    )
}