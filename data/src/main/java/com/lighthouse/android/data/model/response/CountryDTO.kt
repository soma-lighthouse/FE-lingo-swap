package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.CountryVO

data class CountryDTO(
    @SerializedName("code")
    val code: String?,
    @SerializedName("name")
    val name: String?,
) {
    fun toVO() = CountryVO(
        code = code ?: "question",
        name = name ?: "unknown",
        select = false
    )
}