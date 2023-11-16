package com.lighthouse.domain.usecase

import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.domain.logging.ProfileEditLogger
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.ProfileRepository
import com.lighthouse.swm_logging.SWMLogging
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) {
    private lateinit var prev: ProfileVO
    private lateinit var cur: RegisterInfoVO

    private val changed: MutableList<String> = mutableListOf()

    suspend fun invoke(prev: ProfileVO, cur: RegisterInfoVO, duration: Double): Boolean {
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

        sendLog(duration)

        return isUpdated
    }

    private fun checkProfileUpdate(): Boolean {
        var isUpdated = false

        if (prev.description != cur.description) {
            changed.add("description")
            isUpdated = true
        }
        if (cur.profileImageUri!! !in prev.profileImageUri) {
            println("${prev.profileImageUri} ${cur.profileImageUri!!}")
            changed.add("profileImage")
            isUpdated = true
        }
        return isUpdated
    }

    private fun checkFilterUpdate(): Boolean {
        var isUpdated = false
        if (prev.interests.flatMap { it.interests.map { it.code } }
                .toSet() != cur.preferredInterests!!.toSet()) {
            changed.add("interest")
            isUpdated = true
        }

        if (prev.countries.map { it.code } != cur.preferredCountries) {
            changed.add("country")
            isUpdated = true
        }

        return isUpdated
    }

    private fun sendLog(duration: Double) {
        val scheme = ProfileEditLogger.Builder()
            .setUuid(authRepository.getUserId() ?: "")
            .setChanges(changed)
            .setDuration(duration)
            .build()

        SWMLogging.logEvent(scheme)
    }
}
