package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.ErrorVO

data class ErrorDTO(
    @SerializedName("message")
    val message: String?,
    @SerializedName("type")
    val type: String?
) {
    fun toVO() = ErrorVO(
        msg = message ?: "",
        type = type ?: "NONE"
    )
}