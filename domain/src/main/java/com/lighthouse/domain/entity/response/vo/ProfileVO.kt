package com.lighthouse.domain.entity.response.vo

data class ProfileVO(
    val id: String,
    var description: String,
    var profileImageUri: String,
    var languages: List<LanguageVO>,
    val name: String,
    val region: RegionVO,
    var countries: List<CountryVO>,
    var interests: List<InterestVO>,
) {
    constructor() : this("", "", "", listOf(), "", RegionVO(), listOf(), listOf())
}

data class RegionVO(
    val name: String,
    val code: String,
) {
    constructor() : this("", "")
}