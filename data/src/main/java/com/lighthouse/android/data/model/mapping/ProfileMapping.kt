package com.lighthouse.android.data.model.mapping

import com.lighthouse.android.data.model.request.UpdateProfileDTO
import com.lighthouse.android.data.model.request.UploadFilterDTO
import com.lighthouse.domain.entity.request.RegisterInfoVO

fun RegisterInfoVO.toUpdateProfileDTO(): UpdateProfileDTO {
    return UpdateProfileDTO(
        uuid = uuid ?: "",
        profileImageUri = profileImageUri ?: "",
        birthday = birthday ?: "",
        name = name ?: "",
        email = email ?: "",
        gender = gender ?: "",
        description = description ?: "",
        region = region ?: "",
    )
}

fun RegisterInfoVO.toUpdateFilterDTO(): UploadFilterDTO {
    return UploadFilterDTO(
        preferredCountries = preferredCountries ?: emptyList(),
        usedLanguages = languages ?: emptyList(),
        preferredInterests = preferredInterests ?: emptyList(),
    )
}
