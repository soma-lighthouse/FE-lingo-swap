package com.lighthouse.domain.usecase

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.FilterVO
import com.lighthouse.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilterSettingUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    fun invoke(): Flow<Resource<FilterVO>> {
        return homeRepository.getFilterSetting()
    }
}