package com.lighthouse.domain.entity.response.vo

data class MatchProfileVO(
    val id: String,
    val description: String,
    val profileImageUri: String,
    val name: String,
    val region: String,
    val interests: List<String>,
) {
    constructor() : this("", "", "", "", "", listOf())

}