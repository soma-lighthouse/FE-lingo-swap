package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.response.vo.TestVO

data class TestDTO(
    @SerializedName("test")
    val msg: String?,
) {
    fun toVO() = TestVO(
        msg = msg ?: ""
    )
}