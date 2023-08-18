package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.LanguageVO

data class LanguageDTO(
    @SerializedName("name")
    val name: String?,
    @SerializedName("level")
    val level: Int?,
    @SerializedName("code")
    val code: String?,
) {
    fun toVO() = LanguageVO(
        name ?: "Korea",
        level ?: -1,
        code ?: "kr"
    )
}