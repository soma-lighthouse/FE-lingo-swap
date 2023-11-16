package com.lighthouse.android.data.model.mapping

import com.lighthouse.android.data.model.request.UploadFilterDTO
import com.lighthouse.domain.entity.request.UploadFilterVO


fun UploadFilterVO.toDTO(): UploadFilterDTO {
    return UploadFilterDTO(
        preferredCountries = preferredCountries ?: emptyList(),
        preferredInterests = preferredInterests ?: emptyList(),
    )
}