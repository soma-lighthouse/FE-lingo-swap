package com.lighthouse.android.data.model.request

import com.google.gson.annotations.SerializedName

data class UploadFilterDTO(
    @SerializedName("preferredCountries")
    val preferredCountries: List<String>,
    @SerializedName("preferredInterests")
    val preferredInterests: List<String>?
)
