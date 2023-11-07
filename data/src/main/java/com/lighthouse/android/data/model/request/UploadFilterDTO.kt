package com.lighthouse.android.data.model.request

import com.google.gson.annotations.SerializedName
import com.lighthouse.domain.entity.request.UploadInterestVO

data class UploadFilterDTO(
    @SerializedName("preferredCountries")
    val preferredCountries: List<String>,
    @SerializedName("usedLanguages")
    val usedLanguages: List<Map<String, Any>>,
    @SerializedName("preferredInterests")
    val preferredInterests: List<UploadInterestVO>?
)
