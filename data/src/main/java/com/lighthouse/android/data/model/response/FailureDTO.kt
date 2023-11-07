package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName

data class FailureDTO<T>(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val `data`: T,
)