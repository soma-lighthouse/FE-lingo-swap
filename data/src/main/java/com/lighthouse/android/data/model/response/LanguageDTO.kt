package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.LanguageVO

data class LanguageDTO(
    @SerializedName("code")
    val name: String?,
    @SerializedName("level")
    val level: Int?,
) {
    fun toVO() = LanguageVO(
        name ?: "kr",
        level ?: -1
    )
}