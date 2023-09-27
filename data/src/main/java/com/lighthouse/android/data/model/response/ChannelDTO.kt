package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.ChannelVO

data class ChannelDTO(
    @SerializedName("channel_url")
    val id: String?,
) {
    fun toVO() = ChannelVO(
        id = id ?: "",
    )
}