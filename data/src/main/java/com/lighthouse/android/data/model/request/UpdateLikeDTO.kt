package com.lighthouse.android.data.model.request

import com.google.gson.annotations.SerializedName

data class UpdateLikeDTO(
    @SerializedName("memberId")
    val memberId: Int,
)