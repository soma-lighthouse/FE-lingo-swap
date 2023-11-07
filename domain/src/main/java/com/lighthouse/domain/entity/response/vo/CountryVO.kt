package com.lighthouse.domain.entity.response.vo

import java.io.Serializable

data class CountryVO(
    val name: String,
    val code: String,
) : Serializable, Selection()

abstract class Selection(
    var select: Boolean = false,
)