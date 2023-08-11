package com.lighthouse.android.data.model.request

import com.google.gson.annotations.SerializedName

data class UploadQuestionDTO(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("content")
    val content: String,
)