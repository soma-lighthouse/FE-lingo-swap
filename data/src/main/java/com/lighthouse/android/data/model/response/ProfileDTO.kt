package com.lighthouse.android.data.model.response


import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.ProfileVO

data class ProfileDTO(
    @SerializedName("uuid")
    val id: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("profileImageUri")
    val profileImageUri: String?,
    @SerializedName("usedLanguages")
    val languages: List<LanguageDTO>?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("region")
    val region: String?,
    @SerializedName("preferredCountries")
    val countries: List<CountryDTO>?,
    @SerializedName("preferredInterests")
    val interests: List<InterestDTO>?,
) {
    fun toVO() =
        ProfileVO(
            id = id ?: "",
            description = description ?: "",
            profileImageUri = profileImageUri ?: "",
            languages = languages?.map { it.toVO() } ?: listOf(),
            name = name ?: "",
            region = region ?: "",
            countries = countries?.map { it.toVO() } ?: listOf(),
            interests = interests?.map { it.toVO() } ?: listOf()
        )
}