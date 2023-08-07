package com.lighthouse.android.data.model


import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.response.vo.ProfileVO

data class ProfileDTO(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("profileImage")
    val profileImage: String?,
    @SerializedName("languages")
    val languages: List<LanguageDTO>?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("region")
    val region: String?,
    @SerializedName("interests")
    val interests: Map<String, List<String>>,
) {
    fun toVO() =
        ProfileVO(
            id ?: -1,
            description ?: "",
            profileImage ?: "",
            languages?.map { it.toVO() } ?: listOf(),
            name ?: "",
            region ?: "",
            interests ?: mapOf()
        )
}