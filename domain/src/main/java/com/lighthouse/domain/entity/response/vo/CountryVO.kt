package com.lighthouse.domain.entity.response.vo

import java.io.Serializable

data class CountryVO(
    val code: String,
    val name: String,
    var select: Boolean = false,
) : Serializable