package com.lighthouse.domain.usecase

import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.repository.HomeRepository
import javax.inject.Inject

class SaveLanguageFilterUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    fun invoke(languages: List<LanguageVO>) =
        homeRepository.saveLanguageFilter(languages)
}