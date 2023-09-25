package com.lighthouse.domain.usecase

import com.lighthouse.domain.entity.request.UploadFilterVO
import com.lighthouse.domain.repository.HomeRepository
import javax.inject.Inject

class UploadFilterSettingUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    fun invoke(filter: UploadFilterVO) = homeRepository.uploadFilterSetting(filter)
}