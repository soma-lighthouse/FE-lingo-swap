package com.lighthouse.domain.usecase

import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    private lateinit var prev: RegisterInfoVO
    private lateinit var cur: RegisterInfoVO

    fun invoke(prev: RegisterInfoVO, cur: RegisterInfoVO) {
        this.prev = prev
        this.cur = cur
        if (checkProfileUpdate()) {
            profileRepository.updateProfile(cur)
        }
        if (checkFilterUpdate()) {
            profileRepository.updateFilter(cur)
        }
    }

    private fun checkProfileUpdate(): Boolean {
        var isUpdated = false

        if (cur.description != prev.description) {
            cur.description = prev.description
            isUpdated = true
        }
        if (cur.profileImageUri != prev.profileImageUri) {
            cur.profileImageUri = prev.profileImageUri
            isUpdated = true
        }
        return isUpdated
    }

    private fun checkFilterUpdate(): Boolean {
        var isUpdated = false
        if (cur.preferredInterests != prev.preferredInterests) {
            cur.preferredInterests = prev.preferredInterests
            isUpdated = true
        }
        if (cur.languages != prev.languages) {
            cur.languages = prev.languages
            isUpdated = true
        }

        if (cur.preferredCountries != prev.preferredCountries) {
            cur.preferredCountries = prev.preferredCountries
            isUpdated = true
        }

        return isUpdated
    }
}