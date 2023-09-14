package com.lighthouse.android.data.model.request

import com.lighthouse.android.data.model.response.InterestDTO

data class RegisterInfoDTO(
    val uid: String,
    val name: String,
    val birthday: String,
    val email: String,
    val gender: String,
    val region: String,
    val preferredInterests: List<InterestDTO>,
    val description: String,
    val usedLanguages: List<Map<String, Any>>,
    val preferredCountries: List<String>,
    val profileImageUri: String,
)