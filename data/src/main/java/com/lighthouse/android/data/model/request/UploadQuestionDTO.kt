package com.lighthouse.android.data.model.request

import com.google.gson.annotations.SerializedName

data class UploadQuestionDTO(
    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("content")
    val content: String,
)