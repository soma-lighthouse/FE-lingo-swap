package com.lighthouse.android.data.model.request

import com.lighthouse.android.data.model.response.InterestDTO

data class RegisterInfoDTO(
    var name: String,
    var birthday: String,
    var email: String,
    var gender: String,
    var nation: String,
    var preferredInterests: List<InterestDTO>,
    var description: String,
    var languages: List<Map<String, Any>>,
    var preferredCountries: List<String>,
    val profileImage: String,
)