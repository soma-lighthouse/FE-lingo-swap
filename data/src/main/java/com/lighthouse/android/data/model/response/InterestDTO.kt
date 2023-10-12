package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.InterestCategoryVO
import com.lighthouse.domain.entity.response.vo.InterestDetailsVO
import com.lighthouse.domain.entity.response.vo.InterestVO

data class InterestDTO(
    @SerializedName("category")
    val category: InterestCategoryDTO?,
    @SerializedName("interests")
    val interests: List<InterestDetailsDTO>?
) {
    fun toVO() = InterestVO(
        category = category?.toVO() ?: InterestCategoryVO("", ""),
        interests = interests?.map { it.toVO() } ?: listOf()
    )
}

data class InterestDetailsDTO(
    @SerializedName("code")
    val code: String?,
    @SerializedName("name")
    val name: String?,
) {
    fun toVO() = InterestDetailsVO(
        code = code ?: " ",
        name = name ?: " "
    )
}

data class InterestCategoryDTO(
    @SerializedName("code")
    val code: String?,
    @SerializedName("name")
    val name: String?,
) {
    fun toVO() = InterestCategoryVO(
        name = name ?: " ",
        code = code ?: " ",
    )
}