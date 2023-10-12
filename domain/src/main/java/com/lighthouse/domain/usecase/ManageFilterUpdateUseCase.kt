package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.HomeRepository
import javax.inject.Inject

class ManageFilterUpdateUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    fun saveIfFilterUpdated(update: Boolean) = homeRepository.saveIfFilterUpdated(update)
    fun getIfFilterUpdated() = homeRepository.getIfFilterUpdated()
}