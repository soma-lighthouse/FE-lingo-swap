package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.HomeRepository
import javax.inject.Inject

class GetLanguageFilterUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    fun invoke() = homeRepository.getLanguageFilter()
}