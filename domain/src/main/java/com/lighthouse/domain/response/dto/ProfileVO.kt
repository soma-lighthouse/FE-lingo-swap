package com.lighthouse.domain.response.dto

data class ProfileVO(
    val age: Int,
    val description: String,
    val imageUrl: String,
    val interests: Map<String, List<String>>,
    val language: List<Map<String, Int>>,
    val name: String,
    val region: String,
)