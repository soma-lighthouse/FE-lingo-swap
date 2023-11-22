package com.lighthouse.android.data.model.request

data class RegisterInfoDTO(
    val uuid: String,
    val name: String,
    val birthday: String,
    val email: String,
    val gender: String,
    val description: String,
    val region: String,
    val preferredInterests: List<String>,
    val preferredCountries: List<String>,
)