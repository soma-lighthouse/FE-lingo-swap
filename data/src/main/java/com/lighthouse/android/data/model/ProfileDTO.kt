package com.lighthouse.android.data.model


import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.response.dto.ProfileVO

data class ProfileDTO(
    @SerializedName("age")
    val age: Int?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("interests")
    val interests: Map<String, List<String>>?,
    @SerializedName("language")
    val language: List<Map<String, Int>>?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("region")
    val region: String?,
) {
    fun toVO() =
        ProfileVO(
            age ?: 0,
            description ?: "",
            imageUrl ?: "",
            interests ?: mapOf(),
            language ?: listOf(),
            name ?: "",
            region ?: ""
        )
}