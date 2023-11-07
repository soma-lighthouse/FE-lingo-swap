package com.lighthouse.android.data.model.request

data class UpdateProfileDTO(
    val uuid: String,
    val name: String,
    val birthday: String,
    val email: String,
    val gender: String,
    val description: String,
    val region: String,
    val profileImageUri: String,
)
