package com.lighthouse.domain.entity.request

data class UploadFilterVO(
    val preferredCountries: List<String>?,
    val usedLanguages: List<Map<String, Any>>?,
    val preferredInterests: List<UploadInterestVO>?
)
