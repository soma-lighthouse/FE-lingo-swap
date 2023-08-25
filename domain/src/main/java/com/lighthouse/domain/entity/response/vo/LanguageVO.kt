package com.lighthouse.domain.entity.response.vo

import java.io.Serializable

data class LanguageVO(
    var name: String,
    var level: Int,
    var code: String,
) : Serializable