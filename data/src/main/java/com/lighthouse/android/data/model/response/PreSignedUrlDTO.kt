package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.PreSignedUrlVO

data class PreSignedUrlDTO(
    @SerializedName("url")
    val url: String?,
    @SerializedName("endPoint")
    val endPoint: String?
) {
    fun toVO() = PreSignedUrlVO(
        url = url ?: "",
        endPoint = endPoint ?: ""
    )
}