package com.lighthouse.swm_logging

data class BaseDTO(
    val success: Boolean,
    val responseCode: Int,
    val message: String,
    val data: String
)