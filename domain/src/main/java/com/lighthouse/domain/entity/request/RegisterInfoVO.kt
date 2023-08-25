package com.lighthouse.domain.entity.request

import com.lighthouse.domain.entity.response.vo.InterestVO

data class RegisterInfoVO(
    var uuid: String? = null,
    var name: String? = null,
    var birthday: String? = null,
    var email: String? = null,
    var gender: String? = null,
    var region: String? = null,
    var preferredInterests: List<InterestVO>? = null,
    var description: String? = null,
    var languages: List<Map<String, Any>>? = null,
    var preferredCountries: List<String>? = null,
    var profileImageUri: String? = null,
)