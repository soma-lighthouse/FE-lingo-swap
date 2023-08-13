package com.lighthouse.domain.entity.response.vo

data class CountryVO(
    val code: String,
    val name: String,
    var select: Boolean = false,
)