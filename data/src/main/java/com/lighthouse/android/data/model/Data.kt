package com.lighthouse.android.data.model

import com.lighthouse.domain.response.DataVO


data class Data(
    val message: String?
) {
    fun toVO(): DataVO {
        return DataVO(message ?: "Hello world!")
    }
}