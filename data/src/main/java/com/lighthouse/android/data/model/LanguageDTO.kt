package com.lighthouse.android.data.model

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.response.vo.LanguageVO

data class LanguageDTO(
    @SerializedName("code")
    val code: String?,
    @SerializedName("level")
    val level: Int?,
) {
    fun toVO() = LanguageVO(
        code ?: "kr",
        level ?: -1
    )
}