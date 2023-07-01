package com.lighthouse.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.response.PostVO

data class PostsDTO(
    @SerializedName("body")
    val body: String,
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("userId")
    val userId: Int
) {
    fun toVO() = PostVO(
        body ?: "No response given",
        id ?: -1,
        title ?: "No response given",
        userId ?: -1
    )
}