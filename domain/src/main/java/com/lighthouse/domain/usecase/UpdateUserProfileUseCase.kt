package com.lighthouse.domain.usecase

import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    private lateinit var prev: ProfileVO
    private lateinit var cur: RegisterInfoVO

    suspend fun invoke(prev: ProfileVO, cur: RegisterInfoVO): Boolean {
        this.prev = prev
        this.cur = cur

        var isUpdated = true
        if (checkProfileUpdate()) {
            profileRepository.updateProfile(cur)
                .collect {
                    if (!it) {
                        isUpdated = false
                    }
                }
        }
        if (checkFilterUpdate()) {
            profileRepository.updateFilter(cur)
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

        if (prev.description != cur.description) {
            isUpdated = true
        }
        if (prev.profileImageUri != cur.profileImageUri) {
            isUpdated = true
        }
        return isUpdated
    }

    private fun checkFilterUpdate(): Boolean {
        var isUpdated = false
        if (prev.interests.flatMap { it.interests.map { it.code } }
                .toSet() != cur.preferredInterests!!.flatMap { it.interests }.toSet()) {
            isUpdated = true
        }

        if (prev.countries.map { it.code } != cur.preferredCountries) {
            isUpdated = true
        }

        if (prev.languages.map { mapOf("level" to it.level, "code" to it.code) } != cur.languages) {
            isUpdated = true
        }

        return isUpdated
    }

}