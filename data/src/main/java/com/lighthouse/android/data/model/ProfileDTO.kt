package com.lighthouse.android.data.model


import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.response.dto.ProfileVO

data class ProfileDTO(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("age")
    val age: Int?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("language")
    val language: List<Map<String, Int>>?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("region")
    val region: String?,
) {
    fun toVO() =
        ProfileVO(
            id ?: -1,
            age ?: 0,
            description ?: "",
            imageUrl ?: "",
            language ?: listOf(),
            name ?: "",
            region ?: ""
        )
}