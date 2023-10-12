package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.FilterVO

data class FilterDTO(
    @SerializedName("preferredCountries")
    val countries: List<CountryDTO>?,
    @SerializedName("usedLanguages")
    val languages: List<LanguageDTO>?,
    @SerializedName("preferredInterests")
    val interests: List<InterestDTO>?,
) {
    fun toVO() = FilterVO(
        countries = countries?.map { it.toVO() } ?: emptyList(),
        languages = languages?.map { it.toVO() } ?: emptyList(),
        interests = interests?.map { it.toVO() } ?: emptyList(),
    )
}