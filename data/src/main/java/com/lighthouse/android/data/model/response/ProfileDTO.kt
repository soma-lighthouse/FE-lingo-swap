package com.lighthouse.android.data.model.response


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
    @SerializedName("countries")
    val countries: List<String>?,
    @SerializedName("preferredInterests")
    val interests: List<InterestDTO>?,
) {
    fun toVO() =
        ProfileVO(
            id = id ?: -1,
            description = description ?: "",
            profileImage = profileImage ?: "",
            languages = languages?.map { it.toVO() } ?: listOf(),
            name = name ?: "",
            region = region ?: "",
            countries = countries ?: listOf(),
            interests = interests?.map { it.toVO() } ?: listOf()
        )
}