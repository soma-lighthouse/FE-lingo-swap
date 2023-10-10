package com.lighthouse.domain.entity.response.vo

data class ProfileVO(
    val id: String,
    val description: String,
    val profileImageUri: String,
    val languages: List<LanguageVO>,
    val name: String,
    val region: RegionVO,
    val countries: List<CountryVO>,
    val interests: List<InterestVO>,
) {
    constructor() : this("", "", "", listOf(), "", RegionVO(), listOf(), listOf())
}

data class RegionVO(
    val name: String,
    val code: String,
) {
    constructor() : this("", "")
}