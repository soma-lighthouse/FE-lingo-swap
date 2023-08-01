package com.lighthouse.domain.response.dto

data class ProfileVO(
    val id: Int,
    val age: Int,
    val description: String,
    val imageUrl: String,
    val language: List<Map<String, Int>>,
    val name: String,
    val region: String,
)