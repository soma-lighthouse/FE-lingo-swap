package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("data")
    val `data`: T,
)
