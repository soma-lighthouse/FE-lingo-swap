package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.response.vo.InterestVO

data class InterestDTO(
    @SerializedName("category")
    val category: String?,
    @SerializedName("interests")
    val interest: List<String>?,
) {
    fun toVO() = InterestVO(
        category = category ?: " ",
        interest = interest ?: listOf()
    )
}