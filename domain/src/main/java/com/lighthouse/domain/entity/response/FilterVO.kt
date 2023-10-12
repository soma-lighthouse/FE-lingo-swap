package com.lighthouse.domain.entity.response

import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO

data class FilterVO(
    val countries: List<CountryVO>,
    val languages: List<LanguageVO>,
    val interests: List<InterestVO>,
)