package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    fun invoke(userId: Int) = repository.getProfileDetail(userId)
}