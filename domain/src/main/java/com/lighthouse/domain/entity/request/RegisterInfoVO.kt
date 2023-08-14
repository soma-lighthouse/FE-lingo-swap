package com.lighthouse.domain.entity.request

import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO

data class RegisterInfoVO(
    var name: String? = null,
    var birthday: String? = null,
    var email: String? = null,
    var gender: String? = null,
    var nation: String? = null,
    var interest: List<InterestVO>? = null,
    var introduction: String? = null,
    var preferLanguage: List<LanguageVO>? = null,
    var preferCountry: List<CountryVO>? = null,
)