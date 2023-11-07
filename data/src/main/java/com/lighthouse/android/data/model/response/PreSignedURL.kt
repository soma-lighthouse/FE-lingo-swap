package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName

data class PreSignedURL(
    @SerializedName("url")
    val url: String?,
)