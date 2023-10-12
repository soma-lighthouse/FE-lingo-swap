package com.lighthouse.domain.entity.response.vo

data class InterestVO(
    val category: InterestCategoryVO,
    val interests: List<InterestDetailsVO>
)

data class InterestDetailsVO(
    val code: String,
    val name: String,
)

data class InterestCategoryVO(
    val code: String,
    val name: String,
)
