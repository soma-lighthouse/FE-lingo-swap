package com.lighthouse.android.data.model.request

import com.google.gson.annotations.SerializedName

data class CreateChannelDTO(
    @SerializedName("users")
    val userIds: List<String>
)