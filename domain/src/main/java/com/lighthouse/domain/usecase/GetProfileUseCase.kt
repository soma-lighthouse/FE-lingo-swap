package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    fun getProfileDetail(userId: String) = repository.getProfileDetail(userId)

    fun getUUID() = repository.getUUID() ?: ""

    fun setNotification(enabled: Boolean) {
        repository.setPushEnabled(enabled)
    }

    fun getNotification() = repository.getPushEnabled()
}