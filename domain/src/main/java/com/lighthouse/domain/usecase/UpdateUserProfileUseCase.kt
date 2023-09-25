package com.lighthouse.domain.usecase

import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    private lateinit var prev: RegisterInfoVO
    private lateinit var cur: RegisterInfoVO

    suspend fun invoke(prev: RegisterInfoVO, cur: RegisterInfoVO): Boolean {
        this.prev = prev
        this.cur = cur

        var isUpdated = true
        if (checkProfileUpdate()) {
            profileRepository.updateProfile(prev)
                .collect {
                    if (!it) {
                        isUpdated = false
                    }
                }
        }
        if (checkFilterUpdate()) {
            profileRepository.updateFilter(prev)
                .collect {
                    if (!it) {
                        isUpdated = false
                    }
                }
        }

        return isUpdated
    }

    private fun checkProfileUpdate(): Boolean {
        var isUpdated = false

        if (!cur.description.isNullOrEmpty() && prev.description != cur.description) {
            prev.description = cur.description
            isUpdated = true
        }
        if (!cur.profileImageUri.isNullOrEmpty() && prev.profileImageUri != cur.profileImageUri) {
            prev.profileImageUri != cur.profileImageUri
            isUpdated = true
        }
        return isUpdated
    }

    private fun checkFilterUpdate(): Boolean {
        var isUpdated = false
        if (!cur.preferredInterests.isNullOrEmpty() && prev.preferredInterests != cur.preferredInterests) {
            prev.preferredInterests = cur.preferredInterests
            isUpdated = true
        }
        if (!cur.languages.isNullOrEmpty() && prev.languages != cur.languages) {
            prev.languages = cur.languages
            isUpdated = true
        }

        if (!cur.preferredCountries.isNullOrEmpty() && prev.preferredCountries != cur.preferredCountries) {
            prev.preferredCountries = cur.preferredCountries
            isUpdated = true
        }

        return isUpdated
    }

}